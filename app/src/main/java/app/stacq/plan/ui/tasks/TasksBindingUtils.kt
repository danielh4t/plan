package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.CheckBox
import androidx.databinding.BindingAdapter


@BindingAdapter("taskCategoryColor")
fun CheckBox.setColor(categoryColor: String) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(categoryColor))
}