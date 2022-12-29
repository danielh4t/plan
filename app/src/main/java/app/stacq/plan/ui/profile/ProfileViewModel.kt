package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.util.currentYear
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val completed = MutableLiveData<List<Long>>(emptyList())

    val categories: LiveData<List<Category>> = categoryRepository.getCategories()

    fun getCategoryProfileCompleted(categoryId: String) {
        val year = currentYear()
        viewModelScope.launch {
            val documentMap = taskRepository.getCategoryProfileCompleted(categoryId)
                ?.getOrDefault(year, intArrayOf())
            try {
                documentMap?.let {
                    val days = it as List<*>
                    val daysMapped = days.map { day -> day as Long }
                    completed.postValue(daysMapped)
                }
            } catch (_: ClassCastException) {
                completed.postValue(emptyList())
            }
        }
    }
}