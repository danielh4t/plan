package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksLocalDataSource(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTasks(): LiveData<List<Task>> = withContext(ioDispatcher) {
        taskDao.getTasks()
    }

    override suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        taskDao.createTask(task)
    }

    override suspend fun readTaskById(id: String): LiveData<Task> = withContext(ioDispatcher) {
        taskDao.readTaskById(id)
    }

    override suspend fun updateTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        taskDao.deleteById(id)
    }

    override suspend fun updateTaskCompletionById(id: String) = withContext(ioDispatcher) {
        taskDao.updateTaskCompletionById(id)
    }

    override suspend fun updateTaskTimerFinishById(id: String, finishAt: Long) =
        withContext(ioDispatcher) {
            taskDao.updateTaskTimerFinishById(id, finishAt)
        }

    override suspend fun updateTaskTimerAlarmById(id: String) = withContext(ioDispatcher) {
        taskDao.updateTaskTimerAlarmById(id)
    }

    override suspend fun updateTaskPositionById(id: String, positionAt: Long) = withContext(ioDispatcher) {
        taskDao.updateTaskPositionAById(id, positionAt)
    }

    override suspend fun getTasksCategory(): LiveData<List<TaskCategory>> =
        withContext(ioDispatcher) {
            taskDao.getTasksCategory()
        }

    override suspend fun readTaskCategoryById(id: String): LiveData<TaskCategory> =
        withContext(ioDispatcher) {
            taskDao.readTaskCategoryById(id)
        }

}