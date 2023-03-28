package app.stacq.plan.data.source.remote.task

import app.stacq.plan.util.constants.FirestoreConstants.TASKS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class
TaskRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRemoteDataSource {

    override suspend fun create(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId

        if (uid == null || taskId == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "createdAt" to taskDocument.createdAt,
            "name" to taskDocument.name,
            "categoryId" to taskDocument.categoryId,
            "completed" to taskDocument.completed,
            "completedAt" to taskDocument.completedAt,
            "timerFinishAt" to taskDocument.timerFinishAt,
            "timerAlarm" to taskDocument.timerAlarm,
            "priority" to taskDocument.priority,
            "goalId" to taskDocument.goalId,
            "archived" to taskDocument.archived,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(TASKS)
            .document(taskId)
            .set(fields)
    }

    override suspend fun update(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId

        if (uid == null || taskId == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "createdAt" to taskDocument.createdAt,
            "name" to taskDocument.name,
            "categoryId" to taskDocument.categoryId,
            "completed" to taskDocument.completed,
            "completedAt" to taskDocument.completedAt,
            "timerFinishAt" to taskDocument.timerFinishAt,
            "timerAlarm" to taskDocument.timerAlarm,
            "priority" to taskDocument.priority,
            "goalId" to taskDocument.goalId,
            "archived" to taskDocument.archived,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(TASKS)
            .document(taskId)
            .set(fields)
    }


    override suspend fun updateCategory(taskDocument: TaskDocument, previousCategoryId: String) =
        withContext(ioDispatcher) {

            val uid = firebaseAuth.currentUser?.uid
            val taskId = taskDocument.id
            val categoryId = taskDocument.categoryId

            if (uid == null || taskId == null || categoryId == null) return@withContext

            // delete old document
            firestore.collection(uid)
                .document(previousCategoryId)
                .collection(TASKS)
                .document(taskId)
                .delete()

            val fields = hashMapOf(
                "createdAt" to taskDocument.createdAt,
                "name" to taskDocument.name,
                "categoryId" to taskDocument.categoryId,
                "completed" to taskDocument.completed,
                "completedAt" to taskDocument.completedAt,
                "timerFinishAt" to taskDocument.timerFinishAt,
                "timerAlarm" to taskDocument.timerAlarm,
                "priority" to taskDocument.priority,
                "goalId" to taskDocument.goalId,
                "archived" to taskDocument.archived,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection(TASKS)
                .document(taskId)
                .set(fields)
        }

    override suspend fun updatePriority(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId

        if (uid == null || taskId == null || categoryId == null) return@withContext

        val fields = mapOf(
            "priority" to taskDocument.priority,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(TASKS)
            .document(taskId)
            .update(fields)
    }

    override suspend fun updateCompletion(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId

        if (uid == null || taskId == null || categoryId == null) return@withContext

        val fields = mapOf(
            "completed" to taskDocument.completed,
            "completedAt" to taskDocument.completedAt,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(TASKS)
            .document(taskId)
            .update(fields)
    }
}