package app.stacq.plan.ui.tasks


import androidx.lifecycle.*
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _navigateToTask = MutableLiveData<String?>()
    val navigateTask: LiveData<String?> = _navigateToTask

    val tasks: LiveData<List<TaskCategory>> = liveData {
        emitSource(tasksRepository.getTasksCategory())
    }

    fun complete(taskId: String) {
        viewModelScope.launch {
            tasksRepository.updateTaskCompletionById(taskId)
        }
    }

    fun openTask(id: String) {
        _navigateToTask.value = id
    }

    fun closeTask() {
        _navigateToTask.value = null
    }

}