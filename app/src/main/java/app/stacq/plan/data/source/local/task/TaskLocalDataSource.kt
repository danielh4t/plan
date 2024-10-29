package app.stacq.plan.data.source.local.task

import kotlinx.coroutines.flow.Flow


interface TaskLocalDataSource {

    suspend fun create(taskEntity: TaskEntity)

    suspend fun read(taskId: String): TaskEntity?

    suspend fun update(taskEntity: TaskEntity)

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun upsert(taskEntity: TaskEntity)

    fun getTask(): Flow<TaskEntity?>
}
