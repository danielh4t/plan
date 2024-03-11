package app.stacq.plan.data.source.remote.task

interface TaskRemoteDataSource {

    suspend fun create(taskDocument: TaskDocument)

    suspend fun update(taskDocument: TaskDocument)

    suspend fun updateCategory(taskDocument: TaskDocument, previousCategoryId: String)

    suspend fun updatePriority(taskDocument: TaskDocument)

    suspend fun updateStartCompletion(taskDocument: TaskDocument)

    suspend fun getTaskDocuments(categoryId: String): List<TaskDocument>
}