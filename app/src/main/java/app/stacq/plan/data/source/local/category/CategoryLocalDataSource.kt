package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.CategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryLocalDataSource(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryDataSource {


    override suspend fun create(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.insert(categoryEntity)
    }


    override suspend fun updateEnabledById(id: String) {
        categoryDao.updateEnabledById(id)
    }

    override suspend fun delete(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.delete(categoryEntity)
    }

    override suspend fun getCategoriesList(): List<CategoryEntity> = withContext(ioDispatcher) {
        categoryDao.getCategoriesList()
    }

    override fun getCategories(): LiveData<List<CategoryEntity>> {
        return categoryDao.getCategories()
    }
}