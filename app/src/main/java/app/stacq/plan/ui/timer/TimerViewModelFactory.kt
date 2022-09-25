@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.timer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.repository.TasksRepository


class TimerViewModelFactory(
    app: Application,
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TimerViewModel::class.java) ->
                    return TimerViewModel(tasksRepository, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
