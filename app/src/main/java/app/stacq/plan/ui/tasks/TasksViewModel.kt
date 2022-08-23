package app.stacq.plan.ui.tasks

import androidx.lifecycle.*
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class TasksViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    val tasks: LiveData<List<Task>> = liveData {
        emitSource(tasksRepository.getTasks())
    }

    fun complete(task: Task) {
        viewModelScope.launch {
            tasksRepository.complete(task.id, !task.isCompleted)
        }
    }

}