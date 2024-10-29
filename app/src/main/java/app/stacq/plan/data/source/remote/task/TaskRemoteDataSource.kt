package app.stacq.plan.data.source.remote.task

interface TaskRemoteDataSource {

    suspend fun create(taskDocument: TaskDocument)

    suspend fun update(taskDocument: TaskDocument)

    suspend fun getTasks(): List<TaskDocument>
}