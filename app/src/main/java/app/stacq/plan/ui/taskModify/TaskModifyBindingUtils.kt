package app.stacq.plan.ui.taskModify

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("taskCompletionText")
fun TextView.completionTimestampToText(timestamp: Long) {
    if (timestamp != 0L) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = Date(timestamp * 1000)
        text = dateFormat.format(date)
    }
}
