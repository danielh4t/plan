package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryLocalDataSourceImpl (
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryLocalDataSource {

    override suspend fun create(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.insert(categoryEntity)
    }

    override suspend fun update(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.update(categoryEntity)
    }

    override suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.delete(categoryEntity)
    }

    override suspend fun upsert(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.upsert(categoryEntity)
    }

    override suspend fun updateEnabled(categoryId: String) = withContext(ioDispatcher) {
        categoryDao.updateEnabledById(categoryId)
    }
    override suspend fun getCategoriesEntities(): List<CategoryEntity> = withContext(ioDispatcher) {
        categoryDao.getCategoriesEntities()
    }

    override fun getEnabledCategories(): LiveData<List<CategoryEntity>> {
        return categoryDao.getEnabledCategories()
    }

    override fun getCategories(): LiveData<List<CategoryEntity>> {
        return categoryDao.getCategories()
    }

    override fun getAllCategories(): LiveData<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override fun getCategory(categoryId: String): LiveData<CategoryEntity> {
        return categoryDao.getCategory(categoryId)
    }
}