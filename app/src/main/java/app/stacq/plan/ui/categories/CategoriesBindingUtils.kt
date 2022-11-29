package app.stacq.plan.ui.categories

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("categoryColor")
fun ImageView.setColor(color: String) {
    setColorFilter(Color.parseColor(color))
}

