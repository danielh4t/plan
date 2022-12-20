package app.stacq.plan.ui.timer


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.plusSecondsEpoch
import kotlinx.coroutines.launch


class TimerViewModel(
    private val taskRepository: TaskRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    fun updateTaskTimerFinish() {
        val finishAt = plusSecondsEpoch(TimerConstants.TIMER_TIME_IN_SECONDS)
        val update = task.value?.apply { timerFinishAt = finishAt }
        viewModelScope.launch {
            if (update != null) {
                taskRepository.updateTimerFinish(update)
            }
        }
    }

    fun updateTaskTimerAlarm() {
        viewModelScope.launch {
            taskRepository.updateTimerAlarmById(taskId)
        }
    }

}