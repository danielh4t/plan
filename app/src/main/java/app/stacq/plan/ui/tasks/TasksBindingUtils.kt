package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.data.model.Task


@BindingAdapter("taskCategory")
fun TextView.getTaskCategoryTitle(task: Task) {
    val category = task.categoryName
    text = category
}

@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(categoryColor: String) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(categoryColor))
}
