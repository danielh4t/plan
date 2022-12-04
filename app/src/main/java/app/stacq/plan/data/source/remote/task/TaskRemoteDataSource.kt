package app.stacq.plan.data.source.remote.task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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
            "priority" to taskDocument.priority
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection("tasks")
            .document(taskId)
            .set(fields)
    }

    suspend fun update(taskDocument: TaskDocument) = withContext(ioDispatcher) {

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
            "priority" to taskDocument.priority
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection("tasks")
            .document(taskId)
            .set(fields)

    }

    suspend fun updateTaskCompletion(taskDocument: TaskDocument) = withContext(ioDispatcher) {


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
            .collection("tasks")
            .document(taskId)
            .update(fields)

    }

    suspend fun updateTimerFinish(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId

        if (uid == null || taskId == null || categoryId == null) return@withContext

        val fields = mapOf(
            "timerFinishAt" to taskDocument.timerFinishAt,
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection("tasks")
            .document(taskId)
            .update(fields)
    }

}