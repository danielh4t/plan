package app.stacq.plan.ui.create

import androidx.lifecycle.*
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class CreateViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    fun createTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.insert(task)
        }
    }

}