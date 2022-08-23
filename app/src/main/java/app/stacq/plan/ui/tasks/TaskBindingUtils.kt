package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task


@BindingAdapter("taskCategory")
fun TextView.getTaskCategoryTitle(item: Task?) {
    item?.let {
        val category = item.category.name.lowercase().replaceFirstChar { it.uppercase() }
        text = category
    }
}

@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(item: Task) {
    buttonTintList = when (item.category) {
        Category.CODE -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.code))
        Category.HACK -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.hack))
        Category.LIFE -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.life))
        Category.WORK -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.work))
    }
}