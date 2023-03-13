package app.stacq.plan.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.domain.Category
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    categoryId: String
) : ViewModel() {


    val category: LiveData<Category> = categoryRepository.getCategory(categoryId)

    fun delete() {
        val category: Category? = category.value
        category?.let {
            viewModelScope.launch {
                category.deleted = !category.deleted
                categoryRepository.delete(it)
            }
        }
    }

    fun undoDelete() {
        val category: Category? = category.value
        category?.let {
            viewModelScope.launch {
                categoryRepository.create(it)
            }
        }
    }
}