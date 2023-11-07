package app.stacq.plan.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.util.TimeUtil
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val tasksCategory: LiveData<List<Task>> = taskRepository.getTasks()

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun startComplete(task: Task) {
        if (task.startedAt == 0L) {
            task.startedAt = TimeUtil().nowInSeconds()
        } else {
            task.completedAt = if (task.completedAt == 0L) {
                TimeUtil().nowInSeconds()
            } else {
                0L
            }
        }
        viewModelScope.launch {
            taskRepository.updateCompletion(task)
        }
    }

    fun archive(task: Task) {
        viewModelScope.launch {
            taskRepository.archive(task.id)
        }
    }

    fun unarchive(task: Task) {
        viewModelScope.launch {
            taskRepository.unarchive(task.id)
        }
    }
}