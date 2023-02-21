package app.stacq.plan.ui.categories


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.domain.Category
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModel() {

    val categories: LiveData<List<Category>> = categoryRepositoryImpl.getCategories()

    /**
     * On long click of category list item flip enabled
     */
    fun updateEnabled(categoryId: String) {
        viewModelScope.launch {
            categoryRepositoryImpl.updateEnabledById(categoryId)
        }
    }
}