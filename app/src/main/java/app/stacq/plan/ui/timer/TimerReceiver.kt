package app.stacq.plan.ui.timer

import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

import app.stacq.plan.util.sendNotification


class TimerReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        with(NotificationManagerCompat.from(context)) {
            val notificationId: Int =
                intent.getLongExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, 0).toInt()
            val contextText: String =
                intent.getStringExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY).toString()
            sendNotification(notificationId, contextText, context)
        }
    }


}
