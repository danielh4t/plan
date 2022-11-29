package app.stacq.plan.data.source.remote.task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(taskDocument: TaskDocument) = withContext(ioDispatcher) {
        // root collection
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskDocument.id
            val categoryId = taskDocument.categoryId

            if (taskId == null || categoryId == null) return@withContext

            val data = hashMapOf(
                "createdAt" to taskDocument.createdAt,
                "name" to taskDocument.name,
                "categoryId" to taskDocument.categoryId,
                "completed" to taskDocument.completed,
                "completedAt" to taskDocument.completedAt,
                "timerFinishAt" to taskDocument.timerFinishAt,
                "timerAlarm" to taskDocument.timerAlarm
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .set(data)

            firestore.collection(uid)
                .document(categoryId)
                .update("count", FieldValue.increment(1))
        }
    }

    suspend fun update(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskDocument.id
            val categoryId = taskDocument.categoryId

            if (taskId == null || categoryId == null) return@withContext

            val data = hashMapOf(
                "createdAt" to taskDocument.createdAt,
                "name" to taskDocument.name,
                "categoryId" to taskDocument.categoryId,
                "completed" to taskDocument.completed,
                "completedAt" to taskDocument.completedAt,
                "timerFinishAt" to taskDocument.timerFinishAt,
                "timerAlarm" to taskDocument.timerAlarm
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .set(data)
        }
    }

    suspend fun updateTaskCompletion(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskDocument.id
            val categoryId = taskDocument.categoryId

            if (taskId == null || categoryId == null) return@withContext

            val data = mapOf(
                "completed" to taskDocument.completed,
                "completedAt" to taskDocument.completedAt,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .update(data)
        }
    }

    suspend fun updateTimerFinish(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskDocument.id
            val categoryId = taskDocument.categoryId

            if (taskId == null || categoryId == null) return@withContext

            val data = mapOf(
                "timerFinishAt" to taskDocument.timerFinishAt,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .update(data)
        }
    }
}