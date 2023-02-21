package app.stacq.plan.data.repository.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.asTask
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRepository {

    override suspend fun create(task: Task) = withContext(ioDispatcher) {
        localDataSource.create(task.asTaskEntity())
        remoteDataSource.create(task.asTaskDocument())
    }

    override suspend fun read(id: String): Task? = withContext(ioDispatcher) {
        return@withContext localDataSource.read(id)?.asTask()
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        localDataSource.update(task.asTaskEntity())
        remoteDataSource.update(task.asTaskDocument())
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        localDataSource.delete(task.asTaskEntity())
    }

    override suspend fun updateCategory(task: Task, previousCategoryId: String) = withContext(ioDispatcher) {
        localDataSource.update(task.asTaskEntity())
        remoteDataSource.updateCategory(task.asTaskDocument(), previousCategoryId)
    }

    override suspend fun updateCompletion(task: Task) = withContext(ioDispatcher) {
        localDataSource.updateCompletion(task.asTaskEntity())
        remoteDataSource.updateCompletion(task.asTaskDocument())
    }

    override suspend fun updateTimerFinish(task: Task) = withContext(ioDispatcher) {
        localDataSource.updateTimerFinish(task.asTaskEntity())
    }

    override suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        localDataSource.updateTimerAlarmById(id)
    }

    override suspend fun updatePriority(task: Task) = withContext(ioDispatcher) {
        localDataSource.updatePriority(task.asTaskEntity())
        remoteDataSource.updatePriority(task.asTaskDocument())
    }

    override suspend fun getCategoryProfileCompleted(categoryId: String): MutableMap<String, Any>? {
        return remoteDataSource.getCategoryProfileCompleted(categoryId)?.data
    }

    override fun getTasks(): LiveData<List<Task>> =
        Transformations.map(localDataSource.getTasks()) {
            it?.map { it1 -> it1.asTask() }
        }

    override fun getTask(id: String): LiveData<Task> = Transformations.map(localDataSource.getTask(id)) {
        it?.asTask()
    }
}