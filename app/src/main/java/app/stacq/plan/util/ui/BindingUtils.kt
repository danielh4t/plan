package app.stacq.plan.util.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("categoryDropdownColor")
fun ImageView.setImageTint(color: String) {
    imageTintList = ColorStateList.valueOf(Color.parseColor(color))
}