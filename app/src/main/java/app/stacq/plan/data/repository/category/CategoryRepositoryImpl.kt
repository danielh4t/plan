package app.stacq.plan.data.repository.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.asCategory
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryRepositoryImpl(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {

    override suspend fun create(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.create(category.asEntity())
        categoryRemoteDataSource.create(category.asDocument())
    }

    override suspend fun update(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.update(category.asEntity())
        categoryRemoteDataSource.update(category.asDocument())
    }

    override suspend fun delete(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.delete(category.asEntity().id)
        categoryRemoteDataSource.delete(category.asDocument())
    }

    override suspend fun updateEnabled(categoryId: String) {
        categoryLocalDataSource.updateEnabled(categoryId)
    }

    override fun getCount(): LiveData<Int> {
        return categoryLocalDataSource.getCount()
    }

    override fun getEnabledCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getEnabledCategories().map { categoryEntities ->
            categoryEntities.map { categoryEntity -> categoryEntity.asCategory() }
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

    override fun getCategory(categoryId: String): LiveData<Category> {
        return categoryLocalDataSource.getCategory(categoryId)
            .map { categoryEntity -> categoryEntity.asCategory() }
    }
}