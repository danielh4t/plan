package app.stacq.plan.ui.categories


import androidx.lifecycle.*
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.local.category.toCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        emitSource(
            categoryRepository.getCategories()
                .map { categoryEntities -> categoryEntities.map { categoryEntity -> categoryEntity.toCategory() } })
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