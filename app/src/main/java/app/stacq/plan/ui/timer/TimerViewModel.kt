package app.stacq.plan.ui.timer


import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.util.isFinishAtInFuture
import app.stacq.plan.util.millisInFuture
import kotlinx.coroutines.launch
import java.time.Instant


class TimerViewModel(
    private val tasksRepository: TasksRepository,
    private val task: TaskCategory,
    notify: Boolean
) : ViewModel() {

    private val _timerTime = MutableLiveData<String>()
    val timerTime: LiveData<String> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> = _timerFinished

    private val _timerAlarm = MutableLiveData<Boolean>()
    val timerAlarm: LiveData<Boolean> = _timerAlarm

    private val _postNotifications = MutableLiveData<Boolean>()
    val postNotifications: LiveData<Boolean> = _postNotifications

    init {

        _postNotifications.value = notify

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
            tasksRepository.updateTaskTimerFinishById(task.id, finishAt)
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

    fun updateTaskTimerAlarm() {
        task.timerAlarm = !task.timerAlarm
        _timerAlarm.value = !timerAlarm.value!!
        viewModelScope.launch {
            tasksRepository.updateTaskTimerAlarmById(task.id)
        }
    }

}