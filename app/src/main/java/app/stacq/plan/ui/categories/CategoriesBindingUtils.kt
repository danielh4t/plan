package app.stacq.plan.ui.categories

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import app.stacq.plan.data.model.Category


@BindingAdapter("categoryColor")
fun ImageView.setColor(category: Category) {
    setColorFilter(Color.parseColor(category.color))
}

