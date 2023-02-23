package app.stacq.plan.data.repository.category

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.remote.category.CategoryDocument
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import kotlinx.coroutines.flow.Flow

class FakeCategoryRepository: CategoryRepository {

    override suspend fun create(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun updateEnabledById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(categoryEntity: CategoryEntity) {
        TODO("Not yet implemented")
    }

    override fun getEnabledCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun fetchCategories(): Flow<List<CategoryDocument?>> {
        TODO("Not yet implemented")
    }

    override fun getCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getAllCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

}