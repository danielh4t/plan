@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.editTask


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository

class EditTaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(EditTaskViewModel::class.java) ->
                    return EditTaskViewModel(taskRepository, categoryRepository, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
