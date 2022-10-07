package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksRemoteDataSource(
    private val tasksDataStore: TasksDataStore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        tasksDataStore.createTask(task)
    }

}