package app.stacq.plan.ui.timeline

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.util.CalendarUtil
import app.stacq.plan.util.constants.TimerConstants.SECONDS_TO_DAYS
import app.stacq.plan.util.constants.TimerConstants.SECONDS_TO_HOURS
import app.stacq.plan.util.constants.TimerConstants.SECONDS_TO_MINUTES
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@BindingAdapter("timelineLineColor")
fun ImageView.setTimelineLineColor(color: String) {
    backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
}

@BindingAdapter("timelineStartTime")
fun TextView.startTimestampToTime(timestamp: Long) {
    text = if (timestamp == 0L) {
        ""
    } else {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(timestamp * 1000)
        dateFormat.format(date)
    }
}

@BindingAdapter("timelineCompletionDateTime")
fun TextView.creationTimestampToDateTime(timestamp: Long) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(timestamp * 1000)
    text = dateFormat.format(date)
}

@BindingAdapter("timelineStarted", "timelineCompleted")
fun TextView.setDaysDifference(startedAt: Long, completedAt: Long) {
    // in seconds
    val difference = CalendarUtil().startToEndDifferenceInSeconds(startedAt, completedAt).toInt()

    text = when {
        difference < SECONDS_TO_MINUTES -> {
            resources.getQuantityString(
                R.plurals.numberOfSeconds,
                difference,
                difference
            )
        }

        difference < SECONDS_TO_HOURS -> {
            val quantity = difference / SECONDS_TO_MINUTES
            resources.getQuantityString(
                R.plurals.numberOfMinutes,
                quantity,
                quantity
            )
        }

        difference < SECONDS_TO_DAYS -> {
            val quantity = difference / SECONDS_TO_HOURS
            resources.getQuantityString(
                R.plurals.numberOfHours,
                quantity,
                quantity
            )
        }

        else -> {
            val quantity = difference / SECONDS_TO_DAYS
            resources.getQuantityString(R.plurals.numberOfDays, quantity, quantity)
        }
    }
}

@BindingAdapter("timelineHeader")
fun TextView.getTimelineHeader(dayOfWeek: String) {
    text = when (dayOfWeek) {
        "Sunday" -> resources.getString(R.string.day_sunday)
        "Monday" -> resources.getString(R.string.day_monday)
        "Tuesday" -> resources.getString(R.string.day_tuesday)
        "Wednesday" -> resources.getString(R.string.day_wednesday)
        "Thursday" -> resources.getString(R.string.day_thursday)
        "Friday" -> resources.getString(R.string.day_friday)
        "Saturday" -> resources.getString(R.string.day_saturday)
        "Today" -> resources.getString(R.string.today)
        "Yesterday" -> resources.getString(R.string.yesterday)
        else -> dayOfWeek
    }
}

