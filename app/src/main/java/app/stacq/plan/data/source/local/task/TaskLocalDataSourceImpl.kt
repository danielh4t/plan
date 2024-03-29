package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton


@Singleton
class TaskLocalDataSourceImpl(
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

    override suspend fun upsert(taskEntity: TaskEntity) = withContext(ioDispatcher) {
        taskDao.upsert(taskEntity)
    }

    override suspend fun archive(taskId: String) = withContext(ioDispatcher) {
        taskDao.archive(taskId)
    }

    override suspend fun unarchive(taskId: String) {
        taskDao.unarchive(taskId)
    }

    override suspend fun updateStartCompletion(taskEntity: TaskEntity) =
        withContext(ioDispatcher) {
            taskDao.updateStartCompletionById(
                taskEntity.id,
                taskEntity.startedAt,
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

    override suspend fun hasGeneratedTask(goalId: String): Boolean = withContext(ioDispatcher) {
        taskDao.hasGeneratedTask(goalId)
    }

    override suspend fun hasCompletedTaskGoalToday(goalId: String): Boolean =
        withContext(ioDispatcher) {
            taskDao.hasCompletedTaskGoalToday(goalId)
        }

    override fun getCompletedToday(): LiveData<Int> {
        return taskDao.getCompletedToday()
    }

    override fun getCompletedTaskGoalToday(): LiveData<Int> {
        return taskDao.getCompletedTaskGoalToday()
    }

    override fun getCount(): LiveData<Int> {
        return taskDao.getCount()
    }

    override fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>> {
        return taskDao.getTasksAndCategory()
    }

    override fun getTask(taskId: String): LiveData<TaskEntityAndCategoryEntity> {
        return taskDao.getTaskAndCategory(taskId)
    }

    override fun getCompletedTasks(): LiveData<List<TaskEntityAndCategoryEntity>> {
        return taskDao.getCompletedTasksAndCategory()
    }
}