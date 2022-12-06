package app.stacq.plan.util


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import app.stacq.plan.R
import app.stacq.plan.ui.timer.TimerConstants
import app.stacq.plan.ui.timer.TimerReceiver
import java.time.Instant
import java.time.YearMonth
import java.util.*

fun String.sentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun isFinishAtInFuture(finishAt: Long): Boolean {
    val now: Long = Instant.now().epochSecond
    return now > finishAt
}

fun millisInFuture(finishAt: Long): Long {
    val now: Long = Instant.now().epochSecond
    return (finishAt - now) * 1000L
}

fun defaultColors(color: String): String {
    val palette = listOf(
        "#FF74B1",
        "#FFB200",
        "#B2A4FF",
        "#FF00E4",
        "#FF4848",
        "#00EAD3",
        "#FC5404",
        "#F637EC",
        "#4D77FF",
        "#93FFD8"
    )

    return when (color) {
        "Code" -> "#FF7F50"
        "Hack" -> "#2ED573"
        "Life" -> "#FDCD21"
        "Work" -> "#1E90FF"
        else -> palette.random()
    }
}

fun numberOfDays(): Int {
    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val ym = YearMonth.of(year, month)
    return ym.lengthOfMonth()
}


fun startOfDay(day: Int): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, day + 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis / 1000L
}

fun createTimerChannel(applicationContext: Context) {
    val channelId = applicationContext.getString(R.string.timer_channel_id)
    val channelName = applicationContext.getString(R.string.timer_channel_name)
    val description = applicationContext.getString(R.string.timer_channel_description)
    val notificationManager: NotificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channelId, channelName, description)
}

fun setAlarm(applicationContext: Context, finishAt: Long, name: String) {
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent: Intent = Intent(applicationContext, TimerReceiver::class.java)
        .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, finishAt)
        .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, name)

    val notificationPendingIntent =
        createPendingIntent(applicationContext, finishAt, notificationIntent)

    val triggerTime = SystemClock.elapsedRealtime() + millisInFuture(finishAt)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                notificationPendingIntent
            )
        } else {
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }

            applicationContext.startActivity(intent)
        }

    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notificationPendingIntent
        )
    }
}

private fun createPendingIntent(
    applicationContext: Context,
    finishAt: Long,
    notificationIntent: Intent
) = PendingIntent.getBroadcast(
    applicationContext,
    finishAt.toInt(),
    notificationIntent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)


fun cancelAlarm(applicationContext: Context, finishAt: Long, name: String) {
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent: Intent = Intent(applicationContext, TimerReceiver::class.java)
        .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, finishAt)
        .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, name)

    val pendingIntent = createPendingIntent(applicationContext, finishAt, notificationIntent)

    alarmManager.cancel(pendingIntent)
}
