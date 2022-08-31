package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.util.sentenceCase


@BindingAdapter("taskCategory")
fun TextView.getTaskCategoryTitle(taskCategory: TaskCategory) {
        val category = taskCategory.categoryName.sentenceCase()
        text = category

}

@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(taskCategory: TaskCategory) {
    buttonTintList = when (taskCategory.categoryName) {
        "code" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.code))
        "hack" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.hack))
        "life" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.life))
        "work" -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.work))
        else -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.work))
    }
}