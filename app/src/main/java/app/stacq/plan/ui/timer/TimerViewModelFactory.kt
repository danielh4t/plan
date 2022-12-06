@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.repository.TaskRepository


class TimerViewModelFactory(
    private val taskRepository: TaskRepository,
    private val task: Task
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TimerViewModel::class.java) ->
                    return TimerViewModel(taskRepository, task) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
