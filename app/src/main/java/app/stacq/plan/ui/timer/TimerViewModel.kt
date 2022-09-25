package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.*
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch
import java.time.Instant


class TimerViewModel(
    app: Application,
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : AndroidViewModel(app) {


    private val _task = MutableLiveData<TaskCategory?>()
    val task: LiveData<TaskCategory?> = _task

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationPendingIntent: PendingIntent
    private val notificationIntent: Intent


    private val _timerTime = MutableLiveData<String>()
    val timerTime: LiveData<String> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> = _timerFinished


    init {
        viewModelScope.launch {
            _task.value = tasksRepository.readTaskCategoryById(taskId).value
        }

        val finishAt: Long = task.value!!.timerFinishAt
        val title: String = task.value!!.title

        notificationIntent = Intent(app, TimerReceiver::class.java)
            .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, finishAt)
            .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, title)

        notificationPendingIntent =
            PendingIntent.getBroadcast(
                getApplication(),
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val now: Long = Instant.now().epochSecond
        val millisInFuture: Long = (finishAt - now) * 1000L
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timerTime.value = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                _timerFinished.value = true
            }
        }.start()

        val time: Long = millisInFuture / millisInterval
        _timerTime.value = "$time"
        _timerFinished.value = now > task.value!!.timerFinishAt

        if (task.value!!.timerAlarm) {
            setAlarm(millisInFuture)
        }
    }

    fun updateAlarm(isChecked: Boolean) {
        if(isChecked) {
            cancelAlarm()
            viewModelScope.launch {
                tasksRepository.updateTaskTimerAlarmById(taskId)
            }
        }

    }


    private fun setAlarm(millisInFuture: Long) {
        val triggerTime = SystemClock.elapsedRealtime() + millisInFuture
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notificationPendingIntent
        )
    }

    private fun cancelAlarm() {
        alarmManager.cancel(notificationPendingIntent)
    }


}