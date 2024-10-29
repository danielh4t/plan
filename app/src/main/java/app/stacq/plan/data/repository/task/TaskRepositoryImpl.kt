package app.stacq.plan.data.repository.task

import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TaskRepositoryImpl(
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource
) : TaskRepository {

    override suspend fun create(task: Task) = run {
        taskLocalDataSource.create(task.asTaskEntity())
        taskRemoteDataSource.create(task.asTaskDocument())
    }

    override suspend fun read(taskId: String): Task? =
        taskLocalDataSource.read(taskId)?.asTask()


    override suspend fun update(task: Task) = run {
        taskLocalDataSource.update(task.asTaskEntity())
        taskRemoteDataSource.update(task.asTaskDocument())
    }

    override suspend fun delete(task: Task) =
        taskLocalDataSource.delete(task.asTaskEntity())


    override suspend fun upsert(task: Task) =
        taskLocalDataSource.upsert(task.asTaskEntity())

    override fun getTask(): Flow<Task?> =
        taskLocalDataSource.getTask().map {
            it?.asTask()
        }
}
