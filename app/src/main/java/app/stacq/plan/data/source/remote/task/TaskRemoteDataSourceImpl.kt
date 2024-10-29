package app.stacq.plan.data.source.remote.task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class TaskRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRemoteDataSource {

    override suspend fun create(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id

        if (uid == null || taskId == null) return@withContext

        val fields = hashMapOf(
            "id" to taskDocument.id,
            "createdAt" to taskDocument.createdAt,
            "name" to taskDocument.name,
            "completedAt" to taskDocument.completedAt,
            "notes" to taskDocument.notes,
        )

        firestore.collection(uid)
            .document(taskId)
            .set(fields)
    }

    override suspend fun update(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id

        if (uid == null || taskId == null) return@withContext

        val fields = hashMapOf(
            "id" to taskDocument.id,
            "createdAt" to taskDocument.createdAt,
            "name" to taskDocument.name,
            "completedAt" to taskDocument.completedAt,
            "notes" to taskDocument.notes,
        )

        firestore.collection(uid)
            .document(taskId)
            .set(fields)
    }

    override suspend fun getTasks(): List<TaskDocument> {

        val uid = firebaseAuth.currentUser?.uid ?: return emptyList()

        return firestore.collection(uid)
            .get()
            .await()
            .toObjects(TaskDocument::class.java)
            .toList()
    }
}