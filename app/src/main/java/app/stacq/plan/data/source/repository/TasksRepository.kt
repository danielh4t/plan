package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.TasksDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Interface to the data layer.
 */
class TasksRepository(
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTaskAndCategoryName(): LiveData<List<TaskCategory>> {
        return tasksLocalDataSource.getTaskAndCategoryName()
    }

    override suspend fun getTasks(): LiveData<List<Task>> {
        return tasksLocalDataSource.getTasks()
    }

    override suspend fun getTaskCategoryById(id: String): LiveData<TaskCategory> {
        return tasksLocalDataSource.getTaskCategoryById(id)
    }

    override suspend fun getTaskById(id: String): LiveData<Task> {
        return tasksLocalDataSource.getTaskById(id)
    }

    override suspend fun insert(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.insert(task)
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.update(task)
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.delete(task)
    }

    override suspend fun deleteById(id: String)  = withContext(ioDispatcher) {
        tasksLocalDataSource.deleteById(id)
    }

    override suspend fun complete(id: String, isCompleted: Boolean, completedAt: Long) =
        withContext(ioDispatcher) {
            tasksLocalDataSource.complete(id, isCompleted, completedAt)
        }
}