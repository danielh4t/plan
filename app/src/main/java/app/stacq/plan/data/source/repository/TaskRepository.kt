package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskEntity
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
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(task: Task) = withContext(ioDispatcher) {
        localDataSource.create(task.asTaskEntity())
        remoteDataSource.create(task.asTaskDocument())
    }

    suspend fun update(task: Task) = withContext(ioDispatcher) {
        localDataSource.update(task.asTaskEntity())
        remoteDataSource.update(task.asTaskDocument())
    }

    suspend fun delete(task: Task) = withContext(ioDispatcher) {
        localDataSource.delete(task.asTaskEntity())
    }

    suspend fun updateCategory(task: Task, previousCategoryId: String) = withContext(ioDispatcher) {
        localDataSource.update(task.asTaskEntity())
        remoteDataSource.updateCategory(task.asTaskDocument(), previousCategoryId)
    }

    suspend fun updateCompletion(task: Task) = withContext(ioDispatcher) {
        localDataSource.updateCompletion(task.asTaskEntity())
        remoteDataSource.updateTaskCompletion(task.asTaskDocument())
    }

    suspend fun updateTimerFinish(task: Task) = withContext(ioDispatcher) {
        localDataSource.updateTimerFinish(task.asTaskEntity())
        remoteDataSource.updateTimerFinish(task.asTaskDocument())
    }

    suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        localDataSource.updateTimerAlarmById(id)
    }

    suspend fun updatePriority(task: Task) = withContext(ioDispatcher) {
        localDataSource.updatePriority(task.asTaskEntity())
        remoteDataSource.updatePriority(task.asTaskDocument())
    }

    suspend fun getTasks(): LiveData<List<Task>> = withContext(ioDispatcher) {
        localDataSource.getTasks()
            .map { it.map { it1 -> it1.asTask() } }
    }

    suspend fun getTask(id: String): LiveData<Task> = withContext(ioDispatcher) {
        localDataSource.getTask(id).map { it.asTask() }
    }

    suspend fun getTasksList(): List<TaskEntity> = withContext(ioDispatcher) {
        localDataSource.getTasksList()
    }

    suspend fun sync(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        remoteDataSource.update(taskEntity.asTaskDocument())
    }

    suspend fun countCompletedInMonth(startAt: Long): LiveData<List<TaskAnalysis>> = withContext(ioDispatcher) {
        return@withContext localDataSource.countCompletedInMonth(startAt)
    }

}