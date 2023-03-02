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
import javax.inject.Inject


class TaskRepositoryImpl @Inject constructor(
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRepository {

    override suspend fun create(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.create(task.asTaskEntity())
        taskRemoteDataSource.create(task.asTaskDocument())
    }

    override suspend fun read(id: String): Task? = withContext(ioDispatcher) {
        return@withContext taskLocalDataSource.read(id)?.asTask()
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.update(task.asTaskEntity())
        taskRemoteDataSource.update(task.asTaskDocument())
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.delete(task.asTaskEntity())
    }

    override suspend fun updateCategory(task: Task, previousCategoryId: String) = withContext(ioDispatcher) {
        taskLocalDataSource.update(task.asTaskEntity())
        taskRemoteDataSource.updateCategory(task.asTaskDocument(), previousCategoryId)
    }

    override suspend fun updateCompletion(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.updateCompletion(task.asTaskEntity())
        taskRemoteDataSource.updateCompletion(task.asTaskDocument())
    }

    override suspend fun updateTimerFinish(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.updateTimerFinish(task.asTaskEntity())
    }

    override suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        taskLocalDataSource.updateTimerAlarmById(id)
    }

    override suspend fun updatePriority(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.updatePriority(task.asTaskEntity())
        taskRemoteDataSource.updatePriority(task.asTaskDocument())
    }

    override suspend fun getCategoryProfileCompleted(categoryId: String): MutableMap<String, Any>? {
        return taskRemoteDataSource.getCategoryProfileCompleted(categoryId)?.data
    }

    override fun getTasks(): LiveData<List<Task>> =
        Transformations.map(taskLocalDataSource.getTasks()) {
            it?.map { it1 -> it1.asTask() }
        }

    override fun getTask(id: String): LiveData<Task> = Transformations.map(taskLocalDataSource.getTask(id)) {
        it?.asTask()
    }
}