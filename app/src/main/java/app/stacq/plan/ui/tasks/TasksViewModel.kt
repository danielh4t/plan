package app.stacq.plan.ui.tasks


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.toTaskEntity
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.Instant

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val tasks: LiveData<List<Task>> = liveData {
        emitSource(taskRepository.getTasks())
    }

    var categories: Int = 0

    init {
        viewModelScope.launch {
            categories = categoryRepository.getCategoriesCount()
        }
    }

    fun complete(task: Task) {
        task.completed = !task.completed
        task.completedAt = Instant.now().epochSecond
        viewModelScope.launch {
            taskRepository.updateCompletion(task.toTaskEntity())
        }
    }
}