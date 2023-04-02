package app.stacq.plan.ui.timer

import android.graphics.drawable.Animatable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup

@BindingAdapter("timerAlarmCheckBoxVisibility")
fun CheckBox.setVisibility(time: Long) {
    visibility = if (time > 0L) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("timerTextVisibility")
fun TextView.setTimerText(time: Long) {
    visibility = if (time > 0L) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("timerImageVisibility")
fun ImageView.setTimerText(time: Long) {
    if (time > 0L) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        (drawable as Animatable).start()
    }
}

@BindingAdapter("timerButtonsVisibility")
fun MaterialButtonToggleGroup.setVisibility(time: Long) {
    visibility = if (time > 0L) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}