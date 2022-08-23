@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.tasks


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.repository.TasksRepository


class TasksViewModelFactory(private val tasksRepository: TasksRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) ->
                    return TasksViewModel(tasksRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
