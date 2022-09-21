@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TimerViewModelFactory(
    private val finishAt: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TimerViewModel::class.java) ->
                    return TimerViewModel(finishAt) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
