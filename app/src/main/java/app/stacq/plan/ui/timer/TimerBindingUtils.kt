package app.stacq.plan.ui.timer

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("timerTextVisibility")
fun TextView.setVisibility(timerFinished: Boolean) {
    visibility = if (timerFinished) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("timerImageVisibility")
fun ImageView.setVisibility(timerFinished: Boolean) {
    visibility = if (timerFinished) View.VISIBLE else View.INVISIBLE
}