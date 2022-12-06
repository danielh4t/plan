package app.stacq.plan.ui.timer

import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.isFinishAtInFuture
import app.stacq.plan.util.millisInFuture
import kotlinx.coroutines.launch
import java.time.Instant


class TimerViewModel(
    private val taskRepository: TaskRepository,
    private val task: Task
) : ViewModel() {

    private val _timerTime = MutableLiveData<String>()
    val timerTime: LiveData<String> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> = _timerFinished

    private val _timerAlarm = MutableLiveData<Boolean>()
    val timerAlarm: LiveData<Boolean> = _timerAlarm

    init {
        // finish at is not set
        if (task.timerFinishAt == 0L) {
            setFinishAt()
        }

        val isTimerFinished: Boolean = isFinishAtInFuture(task.timerFinishAt)
        _timerFinished.value = isTimerFinished

        // timer not finished
        if (!isTimerFinished) {
            startTimer()
            // set alarm when timer not finished
            _timerAlarm.value = task.timerAlarm
        }
    }

    private fun setFinishAt() {
        val finishAt = Instant.now().plusSeconds(TimerConstants.TIMER_TIME_IN_SECONDS).epochSecond
        task.timerFinishAt = finishAt
        viewModelScope.launch {
            taskRepository.updateTimerFinish(task)
        }
    }

    private fun startTimer() {
        val millisInFuture: Long = millisInFuture(task.timerFinishAt)
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        val time: Long = millisInFuture / millisInterval
        _timerTime.value = "$time"

        object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timerTime.value = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                _timerFinished.value = true
            }
        }.start()
    }

    fun getAlarmTriggerTime(): Long {
        return SystemClock.elapsedRealtime() + millisInFuture(task.timerFinishAt)
    }

    fun updateTaskTimerAlarm() {
        task.timerAlarm = !task.timerAlarm
        _timerAlarm.value = !timerAlarm.value!!
        viewModelScope.launch {
            taskRepository.updateTimerAlarmById(task.id)
        }
    }

}