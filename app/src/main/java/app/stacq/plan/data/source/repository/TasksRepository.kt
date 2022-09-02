package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.TasksDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksRepository(
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val tasksRemoteDataSource: TasksRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTaskCategoryAll(): LiveData<List<TaskCategory>> {
        return tasksLocalDataSource.getTaskCategoryAll()
    }

    override suspend fun getTasks(): LiveData<List<Task>> {
        return tasksLocalDataSource.getTasks()
    }

    override suspend fun getTaskCategoryById(id: String): LiveData<TaskCategory> {
        return tasksLocalDataSource.getTaskCategoryById(id)
    }

    override suspend fun getTaskById(id: String): LiveData<Task> {
        return tasksLocalDataSource.getTaskById(id)
    }

    override suspend fun insert(task: Task) = withContext(ioDispatcher) {
        tasksLocalDataSource.insert(task)
        tasksRemoteDataSource.createTask(task)
    }

    override suspend fun updateTaskTitleAndCategoryById(
        id: String,
        title: String,
        categoryId: Int
    ) = withContext(ioDispatcher) {
        tasksLocalDataSource.updateTaskTitleAndCategoryById(id, title, categoryId)
    }

    override suspend fun deleteById(id: String) = withContext(ioDispatcher) {
        tasksLocalDataSource.deleteById(id)
    }

    override suspend fun updateTaskCompletedById(id: String, completed: Boolean, completedAt: Long) =
        withContext(ioDispatcher) {
            tasksLocalDataSource.updateTaskCompletedById(id, completed, completedAt)
        }
}