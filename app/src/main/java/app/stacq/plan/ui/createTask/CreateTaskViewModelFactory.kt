@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.createTask


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.CategoryRepository
import app.stacq.plan.data.repository.TaskRepository


class CreateTaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(CreateTaskViewModel::class.java) ->
                    return CreateTaskViewModel(taskRepository, categoryRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}