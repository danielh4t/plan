package app.stacq.plan.data.source.local.task

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton


@Singleton
class TaskLocalDataSourceImpl(
    private val taskDao: TaskDao
) : TaskLocalDataSource {

    override suspend fun create(taskEntity: TaskEntity) = taskDao.create(taskEntity)


    override suspend fun read(taskId: String): TaskEntity? = taskDao.read(taskId)


    override suspend fun update(taskEntity: TaskEntity) = taskDao.update(taskEntity)


    override suspend fun delete(taskEntity: TaskEntity) = taskDao.delete(taskEntity)


    override suspend fun upsert(taskEntity: TaskEntity) = taskDao.upsert(taskEntity)

    override fun getTask(): Flow<TaskEntity?> = taskDao.getTask()
}
