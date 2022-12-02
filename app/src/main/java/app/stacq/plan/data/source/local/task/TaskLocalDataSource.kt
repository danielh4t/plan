package app.stacq.plan.data.source.local.task


import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.TaskDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskLocalDataSource(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {

    override suspend fun create(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.create(taskEntity)
    }

    override suspend fun getById(id: String): LiveData<TaskEntity> = withContext(ioDispatcher) {
        taskDao.readById(id)
    }

    override suspend fun update(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.update(taskEntity)
    }

    override suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        taskDao.deleteById(id)
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

    override suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        taskDao.updateTimerAlarmById(id)
    }

    override suspend fun updatePositionById(id: String, positionAt: Long) =
        withContext(ioDispatcher) {
            taskDao.updatePositionAById(id, positionAt)
        }

    override suspend fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>> =
        withContext(ioDispatcher) {
            taskDao.getTasksAndCategory()
        }

    override suspend fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity> =
        withContext(ioDispatcher) {
            taskDao.getTaskAndCategory(id)
        }

}