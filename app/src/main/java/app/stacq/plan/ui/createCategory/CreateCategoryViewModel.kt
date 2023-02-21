package app.stacq.plan.ui.createCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.domain.asCategory
import kotlinx.coroutines.launch

class CreateCategoryViewModel(private val categoryRepositoryImpl: CategoryRepositoryImpl) : ViewModel() {

    fun create(name: String, color: String) {
        viewModelScope.launch {
            val categoryEntity = CategoryEntity(name = name, color = color)
            val category = categoryEntity.asCategory()
            categoryRepositoryImpl.create(category)
        }
    }
}