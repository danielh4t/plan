@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepository


class CategoryViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val categoryId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(CategoryViewModel::class.java) ->
                    return CategoryViewModel(categoryRepository, categoryId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}