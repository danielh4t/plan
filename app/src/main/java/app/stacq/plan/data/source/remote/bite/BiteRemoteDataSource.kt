package app.stacq.plan.data.source.remote.bite

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(biteDocument: BiteDocument) = withContext(ioDispatcher) {

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

    suspend fun update(biteDocument: BiteDocument) = withContext(ioDispatcher) {

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