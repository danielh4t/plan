package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksRepository(
    private val tasksLocalDataSource: TaskLocalDataSource,
    private val tasksRemoteDataSource: TaskRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getTasks(): LiveData<List<Task>> {
        return tasksLocalDataSource.getTasks()
    }

    suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.create(task)
        tasksRemoteDataSource.createTask(task)
    }

    suspend fun readTaskById(id: String): LiveData<Task> {
        return tasksLocalDataSource.readById(id)
    }

    suspend fun updateTask(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.update(task)
        tasksRemoteDataSource.updateTask(task)
    }

    suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.deleteById(id)
    }

    suspend fun updateTaskCompletionById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateCompletionById(id)
    }

    suspend fun updateTaskTimerFinishById(id: String, finishAt: Long) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerFinishById(id, finishAt)
    }

    suspend fun updateTaskTimerAlarmById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTimerAlarmById(id)
    }

    suspend fun updateTaskPositionById(id: String, positionAt: Long) = withContext(ioDispatcher) {
        tasksLocalDataSource.updatePositionById(id, positionAt)
    }

    suspend fun getTasksCategory(): LiveData<List<TaskCategory>> {
        return tasksLocalDataSource.getTasksCategory()
    }

    suspend fun readTaskCategoryById(id: String): LiveData<TaskCategory> {
        return tasksLocalDataSource.readTaskCategoryById(id)
    }

}