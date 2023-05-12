package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.util.time.TimeUtil
import kotlinx.coroutines.launch
import java.time.Instant

class ProfileViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val tasksCategory: LiveData<List<Task>> = taskRepository.getTasks()

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun complete(task: Task) {
        task.completed = !task.completed
        task.completedAt = Instant.now().epochSecond
        task.completedAt = if(task.completed) {
            TimeUtil().nowInSeconds()
        } else {
            0L
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