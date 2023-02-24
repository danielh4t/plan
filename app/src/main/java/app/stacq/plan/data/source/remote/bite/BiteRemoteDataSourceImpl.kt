package app.stacq.plan.data.source.remote.bite

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BiteRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BiteRemoteDataSource {

    override suspend fun create(biteDocument: BiteDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = biteDocument.taskId
        val categoryId = biteDocument.categoryId
        val id = biteDocument.id

        if (uid == null || taskId == null || categoryId == null || id == null) return@withContext

        val fields = hashMapOf(
            "name" to biteDocument.name,
            "taskId" to biteDocument.taskId,
            "categoryId" to biteDocument.categoryId,
            "completed" to biteDocument.completed,
            "completedAt" to biteDocument.completedAt,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection("tasks")
            .document(taskId)
            .collection("bites")
            .document(id)
            .set(fields)

    }

    override  suspend fun update(biteDocument: BiteDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = biteDocument.taskId
        val categoryId = biteDocument.categoryId
        val id = biteDocument.id

        if (uid == null || taskId == null || categoryId == null || id == null) return@withContext

        val fields = hashMapOf(
            "name" to biteDocument.name,
            "taskId" to biteDocument.taskId,
            "categoryId" to biteDocument.categoryId,
            "completed" to biteDocument.completed,
            "completedAt" to biteDocument.completedAt,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection("tasks")
            .document(taskId)
            .collection("bites")
            .document(id)
            .set(fields)
    }
}