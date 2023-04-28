package app.stacq.plan.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.domain.Category


class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    categoryId: String
) : ViewModel() {

    val category: LiveData<Category> = categoryRepository.getCategory(categoryId)
}