package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.asTask
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.model.asTaskDocument
import app.stacq.plan.data.source.model.asTaskEntity
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRepository(
    private val tasksLocalDataSource: TaskLocalDataSource,
    private val tasksRemoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.create(task.asTaskEntity())
        tasksRemoteDataSource.create(task.asTaskDocument())
    }

    suspend fun update(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.update(task.asTaskEntity())
        tasksRemoteDataSource.update(task.asTaskDocument())
    }

    suspend fun delete(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.delete(task.asTaskEntity())
    }

    suspend fun updateCategory(task: Task, previousCategoryId: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.update(task.asTaskEntity())
        tasksRemoteDataSource.updateCategory(task.asTaskDocument(), previousCategoryId)
    }

    suspend fun updateCompletion(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateCompletion(task.asTaskEntity())
        tasksRemoteDataSource.updateTaskCompletion(task.asTaskDocument())
    }

    suspend fun updateTimerFinish(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerFinish(task.asTaskEntity())
        tasksRemoteDataSource.updateTimerFinish(task.asTaskDocument())
    }

    suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerAlarmById(id)
    }

    suspend fun updatePriorityById(id: String, priority: Int) = withContext(ioDispatcher) {
        tasksLocalDataSource.updatePriorityById(id, priority)
    }

    suspend fun getTasks(): LiveData<List<Task>> = withContext(ioDispatcher) {
        tasksLocalDataSource.getTasks()
            .map { it.map { it1 -> it1.asTask() } }
    }

    suspend fun getTask(id: String): LiveData<Task> = withContext(ioDispatcher) {
        tasksLocalDataSource.getTask(id).map { it.asTask() }
    }

}