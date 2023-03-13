package app.stacq.plan.ui.categories

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.CheckBox
import androidx.databinding.BindingAdapter


@BindingAdapter("categoryColor")
fun CheckBox.setColor(color: String) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(color))
}