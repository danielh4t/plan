package app.stacq.plan.ui.timeline


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Task

class TimelineViewModel(
    taskRepository: TaskRepository
) : ViewModel() {

    val tasks: LiveData<List<Task>> = taskRepository.getCompletedTasks()
}
