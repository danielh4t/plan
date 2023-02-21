@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.editTask


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl

class EditTaskViewModelFactory(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    private val categoryRepositoryImpl: CategoryRepositoryImpl,
    private val taskId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(EditTaskViewModel::class.java) ->
                    return EditTaskViewModel(taskRepositoryImpl, categoryRepositoryImpl, taskId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
