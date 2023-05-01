package app.stacq.plan.data.source.remote.goal

import app.stacq.plan.util.constants.FirestoreConstants.GOALS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class GoalRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GoalRemoteDataSource {

    override suspend fun create(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        val fields = hashMapOf(
            "id" to goalDocument.id,
            "name" to goalDocument.name,
            "createdAt" to goalDocument.createdAt,
            "categoryId" to goalDocument.categoryId,
            "days" to goalDocument.days,
            "progress" to goalDocument.progress,
            "completed" to goalDocument.completed,
            "completedAt" to goalDocument.completedAt,
            "generate" to goalDocument.generate,
            "deleted" to goalDocument.deleted,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(GOALS)
            .document(goalId)
            .set(fields)
    }

    override suspend fun update(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        val fields = hashMapOf(
            "id" to goalDocument.id,
            "name" to goalDocument.name,
            "createdAt" to goalDocument.createdAt,
            "categoryId" to goalDocument.categoryId,
            "days" to goalDocument.days,
            "progress" to goalDocument.progress,
            "completed" to goalDocument.completed,
            "completedAt" to goalDocument.completedAt,
            "generate" to goalDocument.generate,
            "deleted" to goalDocument.deleted,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(GOALS)
            .document(goalId)
            .set(fields)
    }

    override suspend fun delete(goalDocument: GoalDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = goalDocument.categoryId
        val goalId = goalDocument.id

        if (uid == null || categoryId == null || goalId == null) return@withContext

        // flips deleted
        val fields = mapOf("deleted" to goalDocument.deleted)

        firestore.collection(uid)
            .document(categoryId)
            .update(fields)
    }

    override suspend fun updateCategory(goalDocument: GoalDocument, previousCategoryId: String) =
        withContext(ioDispatcher) {

            val uid = firebaseAuth.currentUser?.uid
            val categoryId = goalDocument.categoryId
            val goalId = goalDocument.id

            if (uid == null || goalId == null || categoryId == null) return@withContext

            // delete old document
            firestore.collection(uid)
                .document(previousCategoryId)
                .collection(GOALS)
                .document(goalId)
                .delete()

            val fields = hashMapOf(
                "categoryId" to goalDocument.categoryId,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection(GOALS)
                .document(goalId)
                .set(fields)
        }

    override suspend fun getGoalDocuments(categoryId: String): List<GoalDocument> {

        val uid = firebaseAuth.currentUser?.uid ?: return emptyList()

        return firestore.collection(uid)
            .document(categoryId)
            .collection(GOALS)
            .get()
            .await()
            .toObjects(GoalDocument::class.java)
            .toList()
    }
}