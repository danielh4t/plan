package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData

interface CategoryLocalDataSource {

    suspend fun create(categoryEntity: CategoryEntity)

    suspend fun update(categoryEntity: CategoryEntity)

    suspend fun updateEnabledById(id: String)

    suspend fun delete(categoryEntity: CategoryEntity)

    suspend fun getCategoriesEntities(): List<CategoryEntity>

    fun getEnabledCategories(): LiveData<List<CategoryEntity>>

    fun getCategories(): LiveData<List<CategoryEntity>>

    fun getAllCategories(): LiveData<List<CategoryEntity>>
}