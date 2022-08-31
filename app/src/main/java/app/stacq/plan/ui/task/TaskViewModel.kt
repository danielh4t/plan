package app.stacq.plan.ui.task

import androidx.lifecycle.*
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<TaskCategory> = liveData {
        emitSource(tasksRepository.getTaskCategoryById(taskId))
    }

    fun delete() {
        viewModelScope.launch {
            task.value?.let { tasksRepository.deleteById(it.id) }
        }
    }
}