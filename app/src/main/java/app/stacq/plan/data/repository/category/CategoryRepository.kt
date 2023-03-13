package app.stacq.plan.data.repository.category

import androidx.lifecycle.LiveData
import app.stacq.plan.domain.Category

interface CategoryRepository {
    suspend fun create(category: Category)

    suspend fun update(category: Category)

    suspend fun delete(category: Category)

    suspend fun updateEnabled(categoryId: String)

    suspend fun syncCategories()

    fun getEnabledCategories(): LiveData<List<Category>>

    fun getCategories(): LiveData<List<Category>>

    fun getAllCategories(): LiveData<List<Category>>

    fun getCategory(categoryId: String): LiveData<Category>
}