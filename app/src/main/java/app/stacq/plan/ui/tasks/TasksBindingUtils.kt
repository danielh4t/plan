package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.data.model.TaskCategory
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@BindingAdapter("taskCategory")
fun TextView.getTaskCategoryTitle(taskCategory: TaskCategory) {
    val category = taskCategory.categoryName
    text = category
}

@BindingAdapter("taskTime")
fun TextView.getTaskCategoryTitle(epoch: Long) {
    val pattern = "EEEE, dd MMMM , yyyy HH:mm a"
    val dateTime = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochSecond(epoch))
    text = dateTime
}

@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(taskCategory: TaskCategory) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(taskCategory.categoryColor))
}

@BindingAdapter("timerTextVisibility")
fun TextView.setVisibility(timerFinished: Boolean) {
    visibility = if (timerFinished) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("timerImageVisibility")
fun ImageView.setVisibility(timerFinished: Boolean) {
    visibility = if (timerFinished) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("timerFinished", "postNotifications")
fun CheckBox.setVisibility(timerFinished: Boolean, postNotifications: Boolean) {
    visibility = if (timerFinished or !postNotifications) View.INVISIBLE else View.VISIBLE
}
