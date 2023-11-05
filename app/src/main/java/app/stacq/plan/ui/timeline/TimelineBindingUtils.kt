package app.stacq.plan.ui.timeline

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.util.CalendarUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@BindingAdapter("timelineLineColor")
fun ImageView.setTimelineLineColor(color: String) {
    imageTintList = ColorStateList.valueOf(Color.parseColor(color))
}

@BindingAdapter("timelineStartTime")
fun TextView.startTimestampToTime(timestamp: Long) {
    text = if(timestamp == 0L) {
      resources.getString(R.string.ellipsis)
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

@BindingAdapter("timelineCreated", "timelineCompleted")
fun TextView.setDaysDifference(createdAt: Long, completedAt: Long) {
    val difference = CalendarUtil().startToEndDifferenceInDays(createdAt, completedAt).toInt()
    text = if(difference == 0) {
        resources.getString(R.string.today)
    } else {
        resources.getQuantityString(R.plurals.numberOfDays, difference, difference)
    }
}
