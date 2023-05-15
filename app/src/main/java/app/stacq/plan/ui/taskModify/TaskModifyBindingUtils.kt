package app.stacq.plan.ui.taskModify

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.domain.Task
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
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

@BindingAdapter("taskImageViewVisibility")
fun ShapeableImageView.imageViewVisibility(task: Task?) {
    visibility = if (task == null) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("taskTextLayoutVisibility")
fun TextInputLayout.textLayoutVisibility(task: Task?) {
    visibility = if (task == null) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("taskImageIconVisibility")
fun ImageView.imageVisibility(task: Task?) {
    visibility = if (task == null) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("taskImageFabVisibility")
fun ExtendedFloatingActionButton.imageFabVisibility(task: Task?) {
    visibility = if (task == null) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

