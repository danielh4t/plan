package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.CategoryDataSource
import app.stacq.plan.data.source.TasksDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interface to the data layer.
 */
class CategoryRepository(
    private val categoryLocalDataSource: CategoryDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryDataSource {


    override suspend fun getCategories(): LiveData<List<Category>> {
        return categoryLocalDataSource.getCategories()
    }

    override suspend fun insert(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.insert(category)
    }

    override suspend fun delete(category: Category) = withContext(ioDispatcher) {
        categoryLocalDataSource.delete(category)
    }

}