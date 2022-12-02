package app.stacq.plan.ui.task

import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.ui.timer.TimerConstants.TIMER_TIME_IN_SECONDS
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
    val minutes = if (timerFinishAt > 0) {
        millisInFuture(timerFinishAt) / 60 / 1000L
    } else {
        // timer not started
        TIMER_TIME_IN_SECONDS / 60
    }
    text = String.format("%d minutes", minutes)
}