package app.stacq.plan.data.source.remote

import app.stacq.plan.data.source.remote.task.TaskDocument
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import com.google.firebase.firestore.DocumentSnapshot


class FakeTaskRemoteDataSource(private val tasks: MutableList<TaskDocument>? = mutableListOf()):
    TaskRemoteDataSource {
    override suspend fun create(taskDocument: TaskDocument) {
        tasks?.add(taskDocument)
    }

    override suspend fun update(taskDocument: TaskDocument) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(taskDocument: TaskDocument, previousCategoryId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePriority(taskDocument: TaskDocument) {
        tasks?.find { task -> task.id === taskDocument.id}?.priority = taskDocument.priority
    }

    override suspend fun updateCompletion(taskDocument: TaskDocument) {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryProfileCompleted(categoryId: String): DocumentSnapshot? {
        TODO("Not yet implemented")
    }

}