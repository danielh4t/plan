package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.category.CategoryEntity

interface CategoryDataSource {

    suspend fun create(categoryEntity: CategoryEntity)

    suspend fun updateEnabledById(id: String)

    suspend fun delete(categoryEntity: CategoryEntity)

    suspend fun getCategoriesList(): List<CategoryEntity>

    fun getCategories(): LiveData<List<CategoryEntity>>

}