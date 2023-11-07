package app.stacq.plan.ui.tasks

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.databinding.BindingAdapter
import app.stacq.plan.domain.Task
import com.google.android.material.checkbox.MaterialCheckBox


@BindingAdapter("taskCategoryColor")
fun MaterialCheckBox.setColor(categoryColor: String) {
    buttonTintList = ColorStateList.valueOf(Color.parseColor(categoryColor))
}

@BindingAdapter("taskCheckState")
fun MaterialCheckBox.setTaskCheckState(task: Task) {
    checkedState = if (task.startedAt == 0L && task.completedAt == 0L) {
        // Case 1: When both startedAt and completedAt are 0, set the checkbox to neutral state
        MaterialCheckBox.STATE_UNCHECKED
    } else if (task.startedAt != 0L && task.completedAt == 0L) {
        // Case 2: When startedAt is set and completedAt is 0, set the checkbox to false state
        MaterialCheckBox.STATE_INDETERMINATE
    } else {
        // Case 3: When both startedAt and completedAt are set, set the checkbox to true state
        MaterialCheckBox.STATE_CHECKED
    }
}
