package app.stacq.plan.data.repository.task

import app.stacq.plan.domain.Task
import kotlinx.coroutines.flow.Flow


interface TaskRepository {
    suspend fun create(task: Task)

    suspend fun read(taskId: String): Task?

    suspend fun update(task: Task)

    suspend fun delete(task: Task)

    suspend fun upsert(task: Task)

    suspend fun getTask(): Flow<Task?>

    suspend fun getTasks(): Flow<List<Task>>
}
