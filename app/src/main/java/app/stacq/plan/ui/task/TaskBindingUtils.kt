package app.stacq.plan.ui.task

import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.data.model.Task
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
    if (timerFinishAt > 0) {
        val pattern = "EEEE, dd MMMM , yyyy HH:mm a"
        val dateTime = DateTimeFormatter.ofPattern(pattern)
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochSecond(timerFinishAt))
        text = dateTime
    } else {
        // timer not started
        setText(R.string.task_time)
    }
}

@BindingAdapter("taskColorTint")
fun ImageView.setTaskColor(task: Task) {
    setColorFilter(Color.parseColor(task.categoryColor), PorterDuff.Mode.MULTIPLY)
}