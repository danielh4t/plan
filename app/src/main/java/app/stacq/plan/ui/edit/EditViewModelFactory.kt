@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.edit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository

class EditViewModelFactory(
    private val tasksRepository: TasksRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(EditViewModel::class.java) ->
                    return EditViewModel(tasksRepository, categoryRepository, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
