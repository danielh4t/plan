package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository

class TaskViewModel(
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = liveData {
        emitSource(tasksRepository.getTaskById(taskId))
    }

}