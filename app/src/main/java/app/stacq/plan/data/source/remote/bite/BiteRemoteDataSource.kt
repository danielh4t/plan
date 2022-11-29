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

    suspend fun createBite(biteDocument: BiteDocument) = withContext(ioDispatcher) {
        // root collection
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val id = biteDocument.id
            val taskId = biteDocument.taskId
            val categoryId = biteDocument.categoryId

            if (id == null || taskId == null || categoryId == null) return@withContext

            val data = hashMapOf(
                "name" to biteDocument.createdAt,
                "completed" to biteDocument.completed,
                "completedAt" to biteDocument.completedAt,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .collection("bites")
                .document(id)
                .set(data)

        }
    }

}