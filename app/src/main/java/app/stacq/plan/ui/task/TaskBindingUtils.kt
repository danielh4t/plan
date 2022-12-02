package app.stacq.plan.ui.task

import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.ui.timer.TimerConstants.TIMER_TIME_IN_SECONDS
import app.stacq.plan.ui.timer.TimerConstants.TIME_MINUTE_TO_SECONDS
import app.stacq.plan.ui.timer.TimerConstants.TIME_SECOND_TO_MILLIS
import app.stacq.plan.util.millisInFuture
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@BindingAdapter("taskTime")
fun TextView.setTaskTime(epoch: Long) {
    val pattern = "EEEE, dd MMMM , yyyy HH:mm a"
    val dateTime = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochSecond(epoch))
    text = dateTime
}

@BindingAdapter("taskTimer")
fun TextView.setTaskTimer(timerFinishAt: Long) {
    text = if (timerFinishAt == 0L) {
        // timer not started
        val minutes = TIMER_TIME_IN_SECONDS / TIME_MINUTE_TO_SECONDS
        resources.getQuantityString(R.plurals.numberOfMinutes, minutes.toInt(), minutes)
    } else if (millisInFuture(timerFinishAt) > 0L) {
        // timer progress
        val minutes = millisInFuture(timerFinishAt) / TIME_MINUTE_TO_SECONDS / TIME_SECOND_TO_MILLIS
        resources.getQuantityString(R.plurals.numberOfMinutes, minutes.toInt(), minutes)
    } else {
        // timer finished
        resources.getString(R.string.timer_complete)
    }

}