package app.stacq.plan.ui.categoryModify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.asCategory
import kotlinx.coroutines.launch

class CategoryModifyViewModel(
    private val categoryRepository: CategoryRepository,
    categoryId: String?
) : ViewModel() {

    val category: LiveData<Category> = if (categoryId != null) {
        categoryRepository.getCategory(categoryId)
    } else {
        MutableLiveData()
    }

    fun create(name: String, color: String): String {
        val categoryEntity = CategoryEntity(name = name, color = color)
        viewModelScope.launch {
            val category = categoryEntity.asCategory()
            categoryRepository.create(category)
        }
        return categoryEntity.id
    }

    fun update(name: String) {
        viewModelScope.launch {
            category.value?.let {
                it.name = name
                categoryRepository.update(it)
            }
        }
    }
}