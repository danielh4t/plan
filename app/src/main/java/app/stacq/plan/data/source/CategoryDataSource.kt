package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Category

interface CategoryDataSource {

    suspend fun getCategories(): LiveData<List<Category>>

    suspend fun getCategoryIdByName(name: String): String?
    
    suspend fun create(category: Category)

    suspend fun delete(category: Category)
    
}