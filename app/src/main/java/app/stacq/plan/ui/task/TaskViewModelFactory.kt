@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.task


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl


class TaskViewModelFactory(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    private val biteRepositoryImpl: BiteRepositoryImpl,
    private val taskId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TaskViewModel::class.java) ->
                    return TaskViewModel(taskRepositoryImpl, biteRepositoryImpl, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}