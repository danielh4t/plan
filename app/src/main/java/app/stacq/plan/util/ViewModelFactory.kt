@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.util


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.ui.input.InputViewModel
import app.stacq.plan.ui.tasks.TasksViewModel


class ViewModelFactory(private val tasksRepository: TasksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) ->
                    return TasksViewModel(tasksRepository) as T
                isAssignableFrom(InputViewModel::class.java) ->
                    return InputViewModel(tasksRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
