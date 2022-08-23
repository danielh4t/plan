package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
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

    override suspend fun getTasks(): LiveData<List<Task>> {
        return tasksLocalDataSource.getTasks()
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

    override suspend fun complete(taskId: String, isCompleted: Boolean) = withContext(ioDispatcher) {
        tasksLocalDataSource.complete(taskId, isCompleted)
    }

}