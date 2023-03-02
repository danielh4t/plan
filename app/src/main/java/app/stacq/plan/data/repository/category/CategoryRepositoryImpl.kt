package app.stacq.plan.data.repository.category

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
import javax.inject.Inject


class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {

    override suspend fun create(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.create(category.asEntity())
        categoryRemoteDataSource.create(category.asDocument())
    }

    override suspend fun updateEnabledById(id: String) = withContext(ioDispatcher) {
        categoryLocalDataSource.updateEnabledById(id)
    }

    override suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryLocalDataSource.delete(categoryEntity)
    }

    override fun getEnabledCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getEnabledCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
        }
    }

    override fun fetchCategories(): Flow<List<CategoryDocument?>> {
        return categoryRemoteDataSource.getCategories().map {
            it.documents.map { categoryDocument ->
                categoryDocument.toObject(CategoryDocument::class.java)
            }
        }
    }

    override fun getCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
        }
    }

    override fun getAllCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getAllCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
        }
    }
}