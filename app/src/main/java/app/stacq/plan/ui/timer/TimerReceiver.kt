package app.stacq.plan.ui.timer

import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "TimerBroadcastReceiver"

class TimerReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Receive")
    }

}
