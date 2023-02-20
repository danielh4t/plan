package app.stacq.plan.data.source.remote.task

import com.google.firebase.firestore.DocumentSnapshot

interface TaskRemoteDataSource {

    suspend fun create(taskDocument: TaskDocument)

    suspend fun update(taskDocument: TaskDocument)

    suspend fun updateCategory(taskDocument: TaskDocument, previousCategoryId: String)

    suspend fun updatePriority(taskDocument: TaskDocument)

    suspend fun updateCompletion(taskDocument: TaskDocument)

    suspend fun getCategoryProfileCompleted(categoryId: String): DocumentSnapshot?
}