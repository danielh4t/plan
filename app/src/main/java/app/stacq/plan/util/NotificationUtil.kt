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


fun NotificationManagerCompat.sendNotification(notificationId: Int, contentText: String, applicationContext: Context) {

    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
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

    notify(notificationId, builder.build())
}


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