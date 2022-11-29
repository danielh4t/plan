package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interface to the data layer.
 */
class CategoryRepository(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryLocalDataSource.create(categoryEntity)
        categoryRemoteDataSource.createCategory(categoryEntity)
    }

    suspend fun getCategories(): LiveData<List<CategoryEntity>> {
        return categoryLocalDataSource.getCategories()
    }

    suspend fun updateEnabledById(id: String) = withContext(ioDispatcher) {
        categoryLocalDataSource.updateEnabledById(id)
    }

    suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryLocalDataSource.delete(categoryEntity)
    }

    suspend fun getCategoriesCount(): Int = withContext(ioDispatcher) {
        return@withContext categoryLocalDataSource.getCategoriesCount()
    }

}