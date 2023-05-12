@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository


class ProfileViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val goalRepository: GoalRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(ProfileViewModel::class.java) ->
                    return ProfileViewModel(taskRepository, categoryRepository, goalRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}