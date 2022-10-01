package app.stacq.plan.util.ui

import android.content.res.ColorStateList
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
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
    buttonTintList = when (taskCategory.categoryName) {
        "Code" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.code))
        "Hack" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.hack))
        "Life" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.life))
        "Work" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.work))
        else -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.black))
    }
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
