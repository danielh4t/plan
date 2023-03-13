package app.stacq.plan.ui.task

import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("taskCreationDateTime")
fun TextView.creationTimestampToDateTime(timestamp: Long) {
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
    val date = Date(timestamp * 1000)
    text = dateFormat.format(date)
}

@BindingAdapter("taskCompletionDateTime")
fun TextView.completionTimestampToDateTime(timestamp: Long) {
    text= if (timestamp == 0L) {
        resources.getString(R.string.not_completed)
    }else {
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
        val date = Date(timestamp * 1000)
        dateFormat.format(date)
    }
}
