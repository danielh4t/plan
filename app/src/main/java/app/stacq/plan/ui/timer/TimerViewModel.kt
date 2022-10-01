package app.stacq.plan.ui.timer


import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.Instant


class TimerViewModel(
    private val tasksRepository: TasksRepository,
    private val task: TaskCategory
) : ViewModel() {

    private val _timerTime = MutableLiveData<String>()
    val timerTime: LiveData<String> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> = _timerFinished

    private val _timerAlarm = MutableLiveData<Boolean>()
    val timerAlarm: LiveData<Boolean> = _timerAlarm

    private val _notificationPermission = MutableLiveData(true)
    val notificationPermission: LiveData<Boolean> = _notificationPermission

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        // finish at is not set
        if (task.timerFinishAt == 0L) {
            setFinishAt()
        }

        val now: Long = Instant.now().epochSecond
        val isTimerFinished: Boolean = now > task.timerFinishAt
        _timerFinished.value = isTimerFinished

        // timer not finished
        if (!isTimerFinished) {
            startTimer()
            // set alarm only if timer is not finished
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

    fun millisInFuture(finishAt: Long): Long {
        return (finishAt - Instant.now().epochSecond) * 1000L
    }



}