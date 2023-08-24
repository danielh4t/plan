package app.stacq.plan.ui.taskModify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.util.CalendarUtil
import kotlinx.coroutines.launch

class TaskModifyViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository,
    taskId: String?
) : ViewModel() {

    val task: LiveData<Task> = if (taskId !== null) {
        taskRepository.getTask(taskId)
    } else {
        MutableLiveData()
    }

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    private val _selectedCategoryId = MutableLiveData<String>()
    val selectedCategoryId: LiveData<String> = _selectedCategoryId

    fun setSelectedCategoryId(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    val completionCalendar = CalendarUtil()

    fun create(name: String, categoryId: String, notes: String?): String {
        val taskEntity =
            TaskEntity(name = name, categoryId = categoryId, goalId = null, notes = notes)
        viewModelScope.launch {
            val task = taskEntity.asTask()
            taskRepository.create(task)
        }
        return taskEntity.id
    }

    fun update(name: String, categoryId: String, completedAt: Long?, notes: String?) {
        viewModelScope.launch {
            task.value?.let {
                completedAt?.let { timestamp ->
                    it.completed = true
                    it.completedAt = timestamp
                }
                notes?.let { notes ->
                    it.notes = notes
                }
                if (it.categoryId == categoryId) {
                    it.name = name
                    taskRepository.update(it)
                } else {
                    // update category
                    val previousCategoryId = it.categoryId
                    it.categoryId = categoryId
                    taskRepository.updateCategory(it, previousCategoryId)
                }
            }
        }
    }
}