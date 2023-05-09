package app.stacq.plan.ui.taskModify

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.domain.Task
import com.google.android.material.imageview.ShapeableImageView
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

@BindingAdapter("taskImageButtonVisibility")
fun Button.buttonVisibility(task: Task?) {
    visibility = if (task == null) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("taskImageViewVisibility")
fun ShapeableImageView.imageViewVisibility(task: Task?) {
    visibility = if (task == null) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}