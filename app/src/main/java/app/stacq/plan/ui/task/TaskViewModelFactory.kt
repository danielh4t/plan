@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.task


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.repository.TasksRepository


class TaskViewModelFactory(
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TaskViewModel::class.java) ->
                    return TaskViewModel(tasksRepository, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
