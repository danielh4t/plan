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

    override suspend fun getTaskAndCategoryName(): LiveData<List<TaskCategory>> =
        withContext(ioDispatcher) {
            taskDao.getTaskAndCategoryNames()
        }

    override suspend fun getTasks(): LiveData<List<Task>> = withContext(ioDispatcher) {
        taskDao.getTasks()
    }

    override suspend fun getTaskCategoryById(id: String): LiveData<TaskCategory> =
        withContext(ioDispatcher) {
            taskDao.getTaskCategoryById(id)
        }

    override suspend fun getTaskById(id: String): LiveData<Task> = withContext(ioDispatcher) {
        taskDao.getTaskById(id)
    }

    override suspend fun insert(task: Task) = withContext(ioDispatcher) {
        taskDao.insert(task)
    }

    override suspend fun updateTaskTitleAndCategoryById(
        id: String,
        title: String,
        categoryId: Int
    ) = withContext(ioDispatcher) {
        taskDao.updateTaskTitleAndCategoryById(id, title, categoryId)
    }

    override suspend fun delete(task: Task) = withContext(ioDispatcher) {
        taskDao.delete(task)
    }

    override suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        taskDao.deleteById(id)
    }

    override suspend fun complete(id: String, isCompleted: Boolean, completedAt: Long) =
        withContext(ioDispatcher) {
            taskDao.updateTaskIsCompletedById(id, isCompleted, completedAt)
        }
}