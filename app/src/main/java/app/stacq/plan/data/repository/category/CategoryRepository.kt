package app.stacq.plan.data.repository.category

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.remote.category.CategoryDocument
import app.stacq.plan.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun create(category: Category)

    suspend fun updateEnabledById(id: String)

    suspend fun delete(categoryEntity: CategoryEntity)

    fun getEnabledCategories(): LiveData<List<Category>>

    fun fetchCategories(): Flow<List<CategoryDocument?>>

    fun getCategories(): LiveData<List<Category>>

    fun getAllCategories(): LiveData<List<Category>>
}