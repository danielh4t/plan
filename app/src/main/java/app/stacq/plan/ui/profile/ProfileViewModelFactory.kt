@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl


class ProfileViewModelFactory(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    private val categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(ProfileViewModel::class.java) ->
                    return ProfileViewModel(taskRepositoryImpl, categoryRepositoryImpl) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
