package app.stacq.plan.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.stacq.plan.R
import app.stacq.plan.ui.MainActivity


fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: String,
    channelDescription: String
) {

    val channel =
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = channelDescription
        }

    createNotificationChannel(channel)
}

fun createTimerChannel(applicationContext: Context) {
    val channelId = applicationContext.getString(R.string.timer_channel_id)
    val channelName = applicationContext.getString(R.string.timer_channel_name)
    val description = applicationContext.getString(R.string.timer_channel_description)
    val notificationManager: NotificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channelId, channelName, description)
}

fun createBroadcastPendingIntent(
    applicationContext: Context,
    requestCode: Int,
    intent: Intent
): PendingIntent = PendingIntent.getBroadcast(
    applicationContext,
    requestCode,
    intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)

fun NotificationManagerCompat.sendNotification(
    applicationContext: Context,
    requestCode: Int,
    contentText: String,
) {

    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.timer_channel_id)
    )
        .setSmallIcon(R.drawable.ic_checkmark)
        .setContentTitle(applicationContext.getString(R.string.timer_complete))
        .setContentText(contentText)
        .setContentIntent(pendingIntent)

    notify(requestCode, builder.build())
}

