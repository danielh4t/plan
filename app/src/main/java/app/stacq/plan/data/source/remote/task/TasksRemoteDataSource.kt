package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
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

    suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        planApiService.createTask(task)
    }

}