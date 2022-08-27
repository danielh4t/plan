package app.stacq.plan.ui.tasks

import androidx.lifecycle.*
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _navigateToTask = MutableLiveData<String?>()
    val navigateTask: LiveData<String?> = _navigateToTask

    val tasks: LiveData<List<Task>> = liveData {
        emitSource(tasksRepository.getTasks())
    }

    fun complete(task: Task) {
        viewModelScope.launch {
            tasksRepository.complete(task.id, !task.isCompleted, System.currentTimeMillis())
        }
    }

    fun openTask(taskId: String) {
        _navigateToTask.value = taskId
    }

    fun closeTask() {
        _navigateToTask.value = null
    }

}