package app.stacq.plan.ui.categories


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.repository.CategoryRepository
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
            categoryRepository.updateEnabledById(categoryId)
        }
    }

}