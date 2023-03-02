package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CategoryLocalDataSourceImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryLocalDataSource {

    override suspend fun create(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.insert(categoryEntity)
    }

    override suspend fun update(categoryEntity: CategoryEntity) {
        categoryDao.update(categoryEntity)
    }

    override suspend fun updateEnabledById(id: String) {
        categoryDao.updateEnabledById(id)
    }

    override suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.delete(categoryEntity)
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
}