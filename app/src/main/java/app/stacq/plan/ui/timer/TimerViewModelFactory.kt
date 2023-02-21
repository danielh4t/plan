@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.task.TaskRepositoryImpl


class TimerViewModelFactory(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    private val taskId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TimerViewModel::class.java) ->
                    return TimerViewModel(taskRepositoryImpl, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
