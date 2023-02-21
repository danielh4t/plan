@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.createCategory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl


class CreateCategoryViewModelFactory(
    private val categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(CreateCategoryViewModel::class.java) ->
                    return CreateCategoryViewModel(categoryRepositoryImpl) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
