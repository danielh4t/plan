package app.stacq.plan.ui.categories

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import app.stacq.plan.data.source.local.category.Category


@BindingAdapter("categoryColor")
fun ImageView.setColor(category: Category) {
    if (category.enabled) {
        setColorFilter(Color.parseColor(category.color))
    } else {
        setColorFilter(R.color.neutral_70)
    }
}

@BindingAdapter("categoryEnabled")
fun ImageView.setImage(enabled: Boolean) {
    if (enabled) {
        setImageResource(R.drawable.ic_circle_outline)
    } else {
        setImageResource(R.drawable.ic_circle_slash_outline)
    }
}
