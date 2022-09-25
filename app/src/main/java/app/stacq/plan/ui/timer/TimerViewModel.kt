package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import java.time.Instant


class TimerViewModel(
    app: Application,
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : AndroidViewModel(app) {


    val task: LiveData<TaskCategory> = liveData {
        emitSource(tasksRepository.readTaskCategoryById(taskId))
    }

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationPendingIntent: PendingIntent
    private val notificationIntent = Intent(app, TimerReceiver::class.java)


    private val _timerTime = MutableLiveData<String?>()
    val timerTime: LiveData<String?> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean?>()
    val timerFinished: LiveData<Boolean?> = _timerFinished

    private val _millisInFuture = MutableLiveData<Long>()
    val millisInFuture: LiveData<Long> = _millisInFuture

    init {

        notificationPendingIntent =
            PendingIntent.getBroadcast(
                getApplication(),
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val now: Long = Instant.now().epochSecond

        _millisInFuture.value = (task.value!!.timerFinishAt - now) * 1000L
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        object : CountDownTimer(_millisInFuture.value!!, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timerTime.value = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                _timerFinished.value = true
            }
        }.start()

        val time: Long = _millisInFuture.value!! / millisInterval
        _timerTime.value = "$time"
        _timerFinished.value = now > task.value!!.timerFinishAt
    }


    private fun setAlarm(millisInFuture: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + millisInFuture,
            notificationPendingIntent
        )
    }

    private fun cancelAlarm() {
        alarmManager.cancel(notificationPendingIntent)
    }


}