package app.stacq.plan.ui.categories


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    /**
     * On long click of category list item flip enabled
     */
    fun updateEnabled(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.updateEnabledById(categoryId)
        }
    }

}