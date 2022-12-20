package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.yearDays
import app.stacq.plan.util.yearStartAt


class ProfileViewModel(taskRepository: TaskRepository) : ViewModel() {

    val days: Int = yearDays()
    val taskAnalysis: LiveData<List<TaskAnalysis>> = taskRepository.getTaskAnalysis(yearStartAt())

}