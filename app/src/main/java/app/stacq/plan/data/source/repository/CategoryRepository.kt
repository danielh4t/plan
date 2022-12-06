package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.model.Category
import app.stacq.plan.data.source.model.asCategory
import app.stacq.plan.data.source.model.asDocument
import app.stacq.plan.data.source.model.asEntity
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interface to the data layer.
 */
class CategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
    private val remoteDataSource: CategoryRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(category: Category) = withContext(ioDispatcher) {
        localDataSource.create(category.asEntity())
        remoteDataSource.create(category.asDocument())
    }

    suspend fun getCategories(): LiveData<List<Category>> {
        return localDataSource.getCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
        }
    }

    suspend fun updateEnabledById(id: String) = withContext(ioDispatcher) {
        localDataSource.updateEnabledById(id)
    }

    suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        localDataSource.delete(categoryEntity)
    }

    suspend fun getCategoriesCount(): Int = withContext(ioDispatcher) {
        return@withContext localDataSource.getCategoriesCount()
    }

    suspend fun getCategoriesList() = withContext(ioDispatcher) {
        localDataSource.getCategoriesList()
    }

    suspend fun sync(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        remoteDataSource.update(categoryEntity.asDocument())
    }

}