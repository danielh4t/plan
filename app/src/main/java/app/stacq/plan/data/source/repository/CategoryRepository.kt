package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryDocument
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.asCategory
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun updateEnabledById(id: String) = withContext(ioDispatcher) {
        localDataSource.updateEnabledById(id)
    }

    suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        localDataSource.delete(categoryEntity)
    }

    suspend fun getCategoriesEntities() = withContext(ioDispatcher) {
        localDataSource.getCategoriesEntities()
    }

    suspend fun getCategoriesDocuments(): List<CategoryDocument?> = withContext(ioDispatcher) {
        remoteDataSource.getCategoriesDocuments().map { document ->
            document.toObject(CategoryDocument::class.java)
        }
    }

    suspend fun sync(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {
        remoteDataSource.update(categoryDocument)
    }

    fun getCategories(): Flow<List<CategoryDocument?>> {
        return remoteDataSource.getCategories().map {
            it.documents.map { categoryDocument ->
                categoryDocument.toObject(CategoryDocument::class.java)
            }
        }
    }

    fun getEnabledCategories(): LiveData<List<Category>> {
        return localDataSource.getEnabledCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
        }
    }
}