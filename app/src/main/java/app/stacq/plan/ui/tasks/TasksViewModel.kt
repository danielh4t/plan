package app.stacq.plan.ui.tasks


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.Instant

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val tasksCategory: LiveData<List<Task>> = taskRepository.getTasksAndCategory()

    val categories: LiveData<List<Category>> = categoryRepository.getCategories()

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