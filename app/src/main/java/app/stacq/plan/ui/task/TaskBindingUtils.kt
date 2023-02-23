package app.stacq.plan.ui.task

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.util.constants.TimerConstants.TIMER_TIME_IN_SECONDS
import app.stacq.plan.util.constants.TimerConstants.TIME_MINUTE_TO_SECONDS
import app.stacq.plan.util.constants.TimerConstants.TIME_MILLIS_TO_SECONDS
import app.stacq.plan.util.TimeUtil


@BindingAdapter("taskCategoryImage")
fun ImageView.setTaskCategoryImageTime(completed: Boolean) {
    if (completed)
        setImageResource(R.drawable.ic_circle_checkmark)
    else
        setImageResource(R.drawable.ic_circle_outline)
}

@BindingAdapter("taskTimer")
fun TextView.setTaskTimer(timerFinishAt: Long) {
    text = if (timerFinishAt == 0L) {
        // timer not started
        val minutes = TIMER_TIME_IN_SECONDS / TIME_MINUTE_TO_SECONDS
        resources.getQuantityString(R.plurals.numberOfMinutes, minutes.toInt(), minutes)
    } else if (TimeUtil().millisInFuture(timerFinishAt) > 0L) {
        // timer progress
        val minutes =
            TimeUtil().millisInFuture(timerFinishAt) / TIME_MINUTE_TO_SECONDS / TIME_MILLIS_TO_SECONDS
        resources.getQuantityString(R.plurals.numberOfMinutes, minutes.toInt(), minutes)
    } else {
        // timer finished
        resources.getString(R.string.timer_complete)
    }
}