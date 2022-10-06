package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksRemoteDataSource(
    private val planApiService: PlanApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getTasks(): List<Task> = withContext(ioDispatcher) {
        planApiService.getTasks()
    }

    suspend fun createTask(task: Task, tokenId: String) = withContext(ioDispatcher) {
        planApiService.createTask(task)
        planApiService.verifyId(tokenId)
    }

    suspend fun readTaskById(id: String): Task = withContext(ioDispatcher) {
        planApiService.readTaskById(id)
    }

    suspend fun updateTask(task: Task) = withContext(ioDispatcher) {
        planApiService.updateTask(task)
    }

    suspend fun deleteTaskById(id: String) = withContext(ioDispatcher) {
        planApiService.deleteTaskById(id)
    }

    suspend fun getTasksCategory(): List<TaskCategory> = withContext(ioDispatcher) {
        planApiService.getTasksCategory()
    }

    suspend fun readTaskCategoryById(id: String): List<TaskCategory> = withContext(ioDispatcher) {
        planApiService.readTaskCategoryById(id)
    }

}