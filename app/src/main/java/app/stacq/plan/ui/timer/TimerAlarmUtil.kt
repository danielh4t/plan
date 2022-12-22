package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import app.stacq.plan.util.createBroadcastPendingIntent


fun setAlarm(applicationContext: Context, requestCode: Int, name: String, triggerTime: Long) {
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent: Intent = Intent(applicationContext, TimerReceiver::class.java)
        .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, name)
        .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, requestCode)

    val pendingIntent = createBroadcastPendingIntent(applicationContext, requestCode, intent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            val permissionIntent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }
            applicationContext.startActivity(permissionIntent)
        }

    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}

fun cancelAlarm(applicationContext: Context, requestCode: Int, name: String) {
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent: Intent = Intent(applicationContext, TimerReceiver::class.java)
        .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, requestCode)
        .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, name)

    val pendingIntent = createBroadcastPendingIntent(applicationContext, requestCode, intent)

    alarmManager.cancel(pendingIntent)
}