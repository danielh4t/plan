package app.stacq.plan.domain.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import app.stacq.plan.util.constants.TimerConstants
import app.stacq.plan.util.sendNotification


class TimerCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        with(NotificationManagerCompat.from(context)) {
            val requestCode: Int =
                intent.getIntExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, 0)
            val content: String =
                intent.getStringExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY).toString()
            if (areNotificationsEnabled()) {
                sendNotification(context, requestCode, content)
            }
        }
    }
}