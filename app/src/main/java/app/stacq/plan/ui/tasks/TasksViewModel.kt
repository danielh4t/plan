package app.stacq.plan.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import kotlinx.coroutines.launch
import java.time.Instant

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val tasksCategory: LiveData<List<Task>> = taskRepository.getTasks()

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun complete(task: Task) {
        task.completed = !task.completed
        task.completedAt = Instant.now().epochSecond
        viewModelScope.launch {
            taskRepository.updateCompletion(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }
    }
}