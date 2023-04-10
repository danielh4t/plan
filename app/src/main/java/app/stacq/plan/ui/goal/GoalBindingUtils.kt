package app.stacq.plan.ui.goal

import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.util.CalendarUtil
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("goalCreationDateTime")
fun TextView.timestampToDateTime(timestamp: Long) {
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
    val date = Date(timestamp * 1000)
    text = dateFormat.format(date)
}

@BindingAdapter("goalProgress", "goalDays")
fun TextView.setGoalDays(progress: Int, days: Int) {
    text = resources.getQuantityString(R.plurals.numberOfDays, progress, progress)
        .plus(" ")
        .plus(resources.getString(R.string.out_of))
        .plus(" ")
        .plus(resources.getQuantityString(R.plurals.numberOfDays, days, days))
}

@BindingAdapter("daysDifference")
fun TextView.setDaysDifference(createdAt: Long) {
    val difference = CalendarUtil().differenceInDays(createdAt).toInt()
    text = resources.getQuantityString(R.plurals.numberOfDays, difference, difference)
}
