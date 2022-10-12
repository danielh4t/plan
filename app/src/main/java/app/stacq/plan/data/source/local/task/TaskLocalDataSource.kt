package app.stacq.plan.data.source.local.task


import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.TaskDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskLocalDataSource(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {

    override suspend fun getTasks(): LiveData<List<Task>> = withContext(ioDispatcher) {
        taskDao.getTasks()
    }

    override suspend fun create(task: Task) = withContext(ioDispatcher) {
        taskDao.create(task)
    }

    override suspend fun readById(id: String): LiveData<Task> = withContext(ioDispatcher) {
        taskDao.readById(id)
    }

    override suspend fun update(task: Task) = withContext(ioDispatcher) {
        taskDao.update(task)
    }

    override suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        taskDao.deleteById(id)
    }

    override suspend fun updateCompletionById(id: String) = withContext(ioDispatcher) {
        taskDao.updateCompletionById(id)
    }

    override suspend fun updateTimerFinishById(id: String, finishAt: Long) =
        withContext(ioDispatcher) {
            taskDao.updateTimerFinishById(id, finishAt)
        }

    override suspend fun updateTimerAlarmById(id: String) = withContext(ioDispatcher) {
        taskDao.updateTimerAlarmById(id)
    }

    override suspend fun updatePositionById(id: String, positionAt: Long) = withContext(ioDispatcher) {
        taskDao.updatePositionAById(id, positionAt)
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