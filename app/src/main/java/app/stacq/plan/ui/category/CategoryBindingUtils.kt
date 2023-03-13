package app.stacq.plan.ui.category

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("categoryCreationDateTime")
fun TextView.timestampToDateTime(timestamp: Long) {
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
    val date = Date(timestamp * 1000)
    text = dateFormat.format(date)
}