@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository


class TimerViewModelFactory(
    private val tasksRepository: TasksRepository,
    private val task: TaskCategory
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TimerViewModel::class.java) ->
                    return TimerViewModel(
                        tasksRepository,
                        task
                    ) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
