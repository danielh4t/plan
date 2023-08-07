package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.task.TaskRepository

class ProfileViewModel(
    taskRepository: TaskRepository) : ViewModel() {

    val taskCompletedToday: LiveData<Int> = taskRepository.getCompletedToday()

    val taskGoalCompletedToday: LiveData<Int> = taskRepository.getCompletedTaskGoalToday()
}