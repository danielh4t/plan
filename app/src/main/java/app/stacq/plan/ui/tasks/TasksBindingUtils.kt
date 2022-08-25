package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.util.titleCase


@BindingAdapter("taskCategory")
fun TextView.getTaskCategoryTitle(item: Task?) {
    item?.let {
        val category = item.category.name.titleCase()
        text = category
    }
}

@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(item: Task) {
    buttonTintList = when (item.category) {
        Category.Code -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.code))
        Category.Hack -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.hack))
        Category.Life -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.life))
        Category.Work -> ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.work))
    }
}