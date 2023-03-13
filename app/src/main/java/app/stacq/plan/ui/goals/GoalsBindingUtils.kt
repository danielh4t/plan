package app.stacq.plan.ui.goals

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import java.util.*


@BindingAdapter("goalCategoryColor")
fun CheckBox.setColor(categoryColor: String) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(categoryColor))
}