package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton


@Singleton
class TaskLocalDataSourceImpl (
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskLocalDataSource {

    override suspend fun create(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.create(taskEntity)
    }

    override suspend fun read(taskId: String): TaskEntity? = withContext(ioDispatcher) {
        return@withContext taskDao.read(taskId)
    }

    override suspend fun update(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.update(taskEntity)
    }

    override suspend fun delete(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.delete(taskEntity)
    }

    override suspend fun updateCompletion(taskEntity: TaskEntity) =
        withContext(ioDispatcher) {
            taskDao.updateCompletionById(
                taskEntity.id,
                taskEntity.completed,
                taskEntity.completedAt
            )
        }

    override suspend fun updateTimerFinish(taskEntity: TaskEntity) =
        withContext(ioDispatcher) {
            taskDao.updateTimerFinishById(taskEntity.id, taskEntity.timerFinishAt)
        }

    override suspend fun updateTimerAlarmById(taskId: String) = withContext(ioDispatcher) {
        taskDao.updateTimerAlarmById(taskId)
    }

    override suspend fun updatePriority(taskEntity: TaskEntity) =
        withContext(ioDispatcher) {
            taskDao.updatePriority(taskEntity.id, taskEntity.priority)
        }

    override suspend fun getTasksList(): List<TaskEntity> =
        withContext(ioDispatcher) {
            taskDao.getTasksList()
        }

    override fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>> {
        return taskDao.getTasksAndCategory()
    }

    override fun getTask(taskId: String): LiveData<TaskEntityAndCategoryEntity> {
        return taskDao.getTaskAndCategory(taskId)
    }
}