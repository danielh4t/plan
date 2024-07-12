package app.stacq.plan.ui.categories


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.domain.Category
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = categoryRepository.getCategories()

    /**
     * On long click of category list item flip enabled
     */
    fun updateEnabled(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.updateEnabled(categoryId)
        }
    }

    fun delete(category: Category) {
        category.archived = true
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }

    fun undoDelete(category: Category) {
        category.archived = false
        viewModelScope.launch {
            categoryRepository.create(category)
        }
    }
}