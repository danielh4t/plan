package app.stacq.plan.ui.timer

import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import app.stacq.plan.util.NotificationUtil


class TimerReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        with(NotificationManagerCompat.from(context)) {
            val notificationId: Int = intent.getIntExtra("finishAt", 0)
            notify(notificationId, NotificationUtil.buildTimerNotification(context))
        }
    }


}
