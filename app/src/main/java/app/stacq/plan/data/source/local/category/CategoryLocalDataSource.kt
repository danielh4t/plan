package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData

interface CategoryLocalDataSource {

    suspend fun create(categoryEntity: CategoryEntity)

    suspend fun update(categoryEntity: CategoryEntity)

    suspend fun delete(categoryId: String)

    suspend fun upsert(categoryEntity: CategoryEntity)

    suspend fun updateEnabled(categoryId: String)

    suspend fun getCategoryEntities(): List<CategoryEntity>

    fun getCount(): LiveData<Int>

    fun getEnabledCategories(): LiveData<List<CategoryEntity>>

    fun getCategories(): LiveData<List<CategoryEntity>>

    fun getAllCategories(): LiveData<List<CategoryEntity>>
    fun getCategory(categoryId: String): LiveData<CategoryEntity>
}