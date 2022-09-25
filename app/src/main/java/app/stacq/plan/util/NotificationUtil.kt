package app.stacq.plan.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import app.stacq.plan.R
import app.stacq.plan.ui.tasks.TasksFragment


private const val TIMER_CHANNEL_ID = "TIMER_CHANNEL"
private const val TIMER_NOTIFICATION_ID: Int = 0

fun NotificationManager.sendNotification(contentText: String, applicationContext: Context) {

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.timer_channel_id)
    )
        .setSmallIcon(R.drawable.ic_checkmark)
        .setContentTitle(applicationContext.getString(R.string.timer_complete))
        .setContentText(contentText)

    notify(TIMER_NOTIFICATION_ID, builder.build())
}


class NotificationUtil {

    companion object {

        fun buildTimerNotification(context: Context): Notification {

            createTimerNotificationChannel(context)

            val intent = Intent(context, TasksFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            return NotificationCompat.Builder(context, TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_checkmark)
                .setContentTitle(context.getString(R.string.timer_complete))
                .setContentText(context.getString(R.string.complete))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        }

        private fun createTimerNotificationChannel(context: Context) {
            val name = context.getString(R.string.timer_channel_name)
            val descriptionText = context.getString(R.string.timer_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(TIMER_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}