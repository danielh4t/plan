@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.task


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.task.TaskRepository

class TaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val taskId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TaskViewModel::class.java) ->
                    return TaskViewModel(taskRepository, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}