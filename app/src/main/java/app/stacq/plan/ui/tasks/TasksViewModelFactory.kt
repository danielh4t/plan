@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.tasks


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl


class TasksViewModelFactory(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    private val categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) ->
                    return TasksViewModel(taskRepositoryImpl, categoryRepositoryImpl) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
