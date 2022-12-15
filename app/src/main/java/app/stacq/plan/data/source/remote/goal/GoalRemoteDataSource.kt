package app.stacq.plan.data.source.remote.goal

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COLLECTION = "goals"

class GoalRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        val fields = hashMapOf(
            "createdAt" to goalDocument.createdAt,
            "name" to goalDocument.name,
            "categoryId" to goalDocument.categoryId,
            "achieved" to goalDocument.achieved,
            "achievedAt" to goalDocument.achievedAt,
            "achievedBy" to goalDocument.achievedBy,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(COLLECTION)
            .document(goalId)
            .set(fields)
    }

    suspend fun update(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        val fields = hashMapOf(
            "createdAt" to goalDocument.createdAt,
            "name" to goalDocument.name,
            "categoryId" to goalDocument.categoryId,
            "achieved" to goalDocument.achieved,
            "achievedAt" to goalDocument.achievedAt,
            "achievedBy" to goalDocument.achievedBy,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(COLLECTION)
            .document(goalId)
            .set(fields)
    }

    suspend fun updateCompletion(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        val fields = mapOf(
            "achieved" to goalDocument.achieved,
            "achievedAt" to goalDocument.achievedAt,
            "achievedBy" to goalDocument.achievedBy,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(COLLECTION)
            .document(goalId)
            .update(fields)
    }
}