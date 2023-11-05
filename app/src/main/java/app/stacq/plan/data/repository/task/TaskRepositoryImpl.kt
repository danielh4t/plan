package app.stacq.plan.data.repository.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRepositoryImpl(
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRepository {

    override suspend fun create(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.create(task.asTaskEntity())
        taskRemoteDataSource.create(task.asTaskDocument())
    }

    override suspend fun read(taskId: String): Task? = withContext(ioDispatcher) {
        return@withContext taskLocalDataSource.read(taskId)?.asTask()
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.update(task.asTaskEntity())
        taskRemoteDataSource.update(task.asTaskDocument())
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.delete(task.asTaskEntity())
    }

    override suspend fun archive(taskId: String) = withContext(ioDispatcher) {
        taskLocalDataSource.archive(taskId)
    }

    override suspend fun unarchive(taskId: String) = withContext(ioDispatcher) {
        taskLocalDataSource.unarchive(taskId)
    }

    override suspend fun updateCategory(task: Task, previousCategoryId: String) =
        withContext(ioDispatcher) {
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

    override suspend fun updateTimerAlarmById(taskId: String) = withContext(ioDispatcher) {
        taskLocalDataSource.updateTimerAlarmById(taskId)
    }

    override suspend fun updatePriority(task: Task) = withContext(ioDispatcher) {
        taskLocalDataSource.updatePriority(task.asTaskEntity())
        taskRemoteDataSource.updatePriority(task.asTaskDocument())
    }

    override suspend fun hasGeneratedTask(goalId: String): Boolean = withContext(ioDispatcher) {
        return@withContext taskLocalDataSource.hasGeneratedTask(goalId)
    }

    override suspend fun hasCompletedTaskGoalToday(goalId: String): Boolean =
        withContext(ioDispatcher) {
            return@withContext taskLocalDataSource.hasCompletedTaskGoalToday(goalId)
        }

    override fun getCompletedToday(): LiveData<Int> = taskLocalDataSource.getCompletedToday()

    override fun getCompletedTaskGoalToday(): LiveData<Int> =
        taskLocalDataSource.getCompletedTaskGoalToday()

    override fun getCount(): LiveData<Int> = taskLocalDataSource.getCount()

    override fun getTasks(): LiveData<List<Task>> =
        taskLocalDataSource.getTasks().map {
            it.map { it1 -> it1.asTask() }
        }

    override fun getTask(taskId: String): LiveData<Task> =
        taskLocalDataSource.getTask(taskId).map {
            it.asTask()
        }

    override fun getCompletedTasks(): LiveData<List<Task>> =
        taskLocalDataSource.getCompletedTasks().map {
            it.map { it1 -> it1.asTask() }
        }
}