package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.CategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryLocalDataSource(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryDataSource {

    override suspend fun getCategories(): LiveData<List<Category>> = withContext(ioDispatcher) {
        categoryDao.getCategories()
    }

    override suspend fun getCategoryIdByName(name: String): Int? = withContext(ioDispatcher) {
        categoryDao.getCategoryIdByName(name)
    }

    override suspend fun insert(category: Category) = withContext(ioDispatcher) {
        categoryDao.insert(category)
    }

    override suspend fun delete(category: Category) = withContext(ioDispatcher) {
        categoryDao.delete(category)
    }

}