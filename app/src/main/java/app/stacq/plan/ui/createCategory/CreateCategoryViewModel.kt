package app.stacq.plan.ui.createCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.repository.CategoryRepository
import app.stacq.plan.domain.asCategory
import kotlinx.coroutines.launch

class CreateCategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    fun create(name: String, color: String) {
        viewModelScope.launch {
            val categoryEntity = CategoryEntity(name = name, color = color)
            val category = categoryEntity.asCategory()
            categoryRepository.create(category)
        }
    }
}