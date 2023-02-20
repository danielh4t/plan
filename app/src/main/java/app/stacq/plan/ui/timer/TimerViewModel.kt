package app.stacq.plan.ui.timer


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.TaskRepository
import app.stacq.plan.domain.Task
import app.stacq.plan.util.TimeUtil
import kotlinx.coroutines.launch


class TimerViewModel(
    private val taskRepository: TaskRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val time: MutableLiveData<Long> = MutableLiveData()

    fun setTaskTimerFinish() {
        val finishAt = TimeUtil().plusSecondsEpoch(TimerConstants.TIMER_TIME_IN_SECONDS)
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