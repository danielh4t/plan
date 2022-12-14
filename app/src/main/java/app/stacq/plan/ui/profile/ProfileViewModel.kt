package app.stacq.plan.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.remote.category.CategoryDocument
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.CalendarUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: Flow<List<CategoryDocument?>> = categoryRepository.getCategories()

    val completedMap = MutableLiveData<MutableMap<String, List<Int>>>(mutableMapOf())

    fun getCategoryProfileCompleted(categoryId: String) {
        val year = CalendarUtil().currentYear()
        viewModelScope.launch {
            val completed = taskRepository.getCategoryProfileCompleted(categoryId)
                ?.getOrDefault(year, intArrayOf())
            try {
                completed?.let {
                    val categoryCompletedList = (it as List<*>).map { day -> (day as Long).toInt() }
                    // Get existing value of completed
                    completedMap.value?.let { current ->
                        // Update entries with category
                        current[categoryId] = categoryCompletedList
                        // Overwrite with updated values
                        completedMap.value = current
                    }
                }

            } catch (e: ClassCastException) {
                completedMap.postValue(mutableMapOf())
            }
        }
    }

    fun combine(completedMap: MutableMap<String, List<Int>>): List<Int> {
        val combined = IntArray(CalendarUtil().days()) { 0 }.asList().toMutableList()
        completedMap.map {
            it.value.mapIndexed { idx, value ->
                combined[idx] = combined[idx] + value
            }
        }
        return combined
    }
}