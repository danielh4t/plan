package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.asTask
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    }

    suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        localDataSource.updateTimerAlarmById(id)
    }

    suspend fun updatePriority(task: Task) = withContext(ioDispatcher) {
        localDataSource.updatePriority(task.asTaskEntity())
        remoteDataSource.updatePriority(task.asTaskDocument())
    }

    suspend fun getTasksList(): List<TaskEntity> = withContext(ioDispatcher) {
        localDataSource.getTasksList()
    }

    suspend fun sync(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        remoteDataSource.update(taskEntity.asTaskDocument())
    }

    fun getTasksAndCategory(): LiveData<List<Task>> =
        Transformations.map(localDataSource.getTasksAndCategory()) {
            it?.map { it1 -> it1.asTask() }
        }

    fun getTask(id: String): LiveData<Task> = Transformations.map(localDataSource.getTask(id)) {
        it?.asTask()
    }

    fun getProfileCompleted(categoryId: String): Flow<MutableMap<String, Any>?> {
        return remoteDataSource.getProfileCompleted(categoryId).map {
            it?.data
        }
    }
}