package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.CategoryDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interface to the data layer.
 */
class CategoryRepository(
    private val categoryLocalDataSource: CategoryDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryDataSource {


    override suspend fun getCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getCategories()
    }

    override suspend fun getCategoryIdByName(name: String): String? = withContext(ioDispatcher) {
        categoryLocalDataSource.getCategoryIdByName(name)
    }

    override suspend fun create(category: Category): Unit = withContext(ioDispatcher) {
        categoryLocalDataSource.create(category)
        categoryRemoteDataSource.createCategory(category)
    }

    override suspend fun delete(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.delete(category)
    }

}