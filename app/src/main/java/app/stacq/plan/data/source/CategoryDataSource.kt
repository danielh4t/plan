package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.category.CategoryEntity

interface CategoryDataSource {

    suspend fun create(categoryEntity: CategoryEntity)

    suspend fun update(categoryEntity: CategoryEntity)

    suspend fun updateEnabledById(id: String)

    suspend fun delete(categoryEntity: CategoryEntity)

    suspend fun getCategoriesEntityList(): List<CategoryEntity>

    fun getEnabledCategories(): LiveData<List<CategoryEntity>>

    fun getCategories(): LiveData<List<CategoryEntity>>

}