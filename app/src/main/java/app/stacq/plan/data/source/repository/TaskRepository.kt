package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.toTaskDocument
import app.stacq.plan.data.model.toTaskEntity
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.toTaskDocument
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRepository(
    private val tasksLocalDataSource: TaskLocalDataSource,
    private val tasksRemoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        tasksLocalDataSource.create(taskEntity)
        tasksRemoteDataSource.create(taskEntity.toTaskDocument())
    }

    suspend fun getTasks(): LiveData<List<Task>> {
        return tasksLocalDataSource.getTasks()
    }

    suspend fun update(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.update(task.toTaskEntity())
        tasksRemoteDataSource.update(task.toTaskDocument())
    }

    suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.deleteById(id)
    }

    suspend fun updateCompletion(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateCompletion(taskEntity)
        tasksRemoteDataSource.updateTaskCompletion(taskEntity.toTaskDocument())
    }

    suspend fun updateTimerFinish(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerFinish(taskEntity)
        tasksRemoteDataSource.updateTimerFinish(taskEntity.toTaskDocument())
    }

    suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerAlarmById(id)
    }

    suspend fun getTaskCategoryById(id: String): LiveData<Task> {
        return tasksLocalDataSource.getTaskCategoryById(id)
    }

}