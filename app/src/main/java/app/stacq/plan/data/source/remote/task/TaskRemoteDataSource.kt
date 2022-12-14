package app.stacq.plan.data.source.remote.task

import android.util.Log
import app.stacq.plan.util.CalendarUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val TASKS = "tasks"
private const val PROFILE = "profile"
private const val COMPLETED = "completed"

class TaskRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(taskDocument: TaskDocument) = withContext(ioDispatcher) {

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
            .collection(TASKS)
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
            .collection(TASKS)
            .document(taskId)
            .set(fields)
    }


    suspend fun updateCategory(taskDocument: TaskDocument, previousCategoryId: String) =
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
                "priority" to taskDocument.priority
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection(TASKS)
                .document(taskId)
                .set(fields)
        }

    suspend fun updatePriority(taskDocument: TaskDocument) = withContext(ioDispatcher) {

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

    suspend fun updateCompletion(taskDocument: TaskDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val taskId = taskDocument.id
        val categoryId = taskDocument.categoryId
        val completed = taskDocument.completed

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

        val document = firestore.collection(uid)
            .document(categoryId)
            .collection(PROFILE)
            .document(COMPLETED)
            .get()
            .await()

        Log.d("doc", document.toString())

        var completedMutable: MutableList<Long> = mutableListOf()
        try {
            val data = document.data?.get(CalendarUtil().currentYear())
            data?.let {
                val dataList = it as List<*>
                val completedList = dataList.map { item -> item as Long }
                completedMutable = completedList.toMutableList()
                // Update completed count on current day.
                // Decrement to handle zero-based index since first day is 1
                val day = CalendarUtil().day() - 1
                if (completed) {
                    completedMutable[day] = completedMutable[day] + 1
                } else {
                    completedMutable[day] = completedMutable[day] - 1
                }
            }
        } catch (e: ClassCastException) {
            return@withContext
        }

        if (completedMutable.isEmpty()) return@withContext

        firestore.collection(uid)
            .document(categoryId)
            .collection(PROFILE)
            .document(COMPLETED)
            .update(CalendarUtil().currentYear(), completedMutable)
    }

    suspend fun getCategoryProfileCompleted(categoryId: String): DocumentSnapshot? {

        val uid = firebaseAuth.currentUser?.uid ?: return null

        return firestore.collection(uid)
            .document(categoryId)
            .collection(PROFILE)
            .document(COMPLETED)
            .get()
            .await()
    }
}