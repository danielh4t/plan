package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
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

    override suspend fun getTaskById(taskId: String): LiveData<Task> = withContext(ioDispatcher) {
        taskDao.getTaskById(taskId)
    }

    override suspend fun insert(task: Task) = withContext(ioDispatcher) {
        taskDao.insert(task)
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        taskDao.update(task)
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        taskDao.delete(task)
    }

    override suspend fun complete(taskId: String, isCompleted: Boolean, completedAt: Long) =
        withContext(ioDispatcher) {
            taskDao.updateTaskIsCompletedById(taskId, isCompleted, completedAt)
        }
}