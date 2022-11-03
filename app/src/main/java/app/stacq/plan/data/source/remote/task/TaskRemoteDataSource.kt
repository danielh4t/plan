package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
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

    suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        // root collection
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val categoryId = task.categoryId

            val data = hashMapOf(
                "createdAt" to task.createdAt,
                "title" to task.title,
                "categoryId" to task.categoryId,
                "completed" to task.completed,
                "completedAt" to task.completedAt,
                "timerFinishAt" to task.timerFinishAt,
                "timerAlarm" to task.timerAlarm
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(task.id)
                .set(data)

            firestore.collection(uid)
                .document(categoryId)
                .update("count", FieldValue.increment(1))
        }
    }

    suspend fun updateTask(task: Task) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val categoryId = task.categoryId

            val data = hashMapOf(
                "createdAt" to task.createdAt,
                "title" to task.title,
                "categoryId" to task.categoryId,
                "completed" to task.completed,
                "completedAt" to task.completedAt,
                "timerFinishAt" to task.timerFinishAt,
                "timerAlarm" to task.timerAlarm
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(task.id)
                .set(data)
        }
    }

    suspend fun updateTaskCompletion(taskCategory: TaskCategory) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskCategory.id
            val categoryId = taskCategory.categoryId

            val data = mapOf(
                "completed" to taskCategory.completed,
                "completedAt" to taskCategory.completedAt,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .update(data)
        }
    }

    suspend fun updateTaskFinish(taskCategory: TaskCategory) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {

            val taskId = taskCategory.id
            val categoryId = taskCategory.categoryId

            val data = mapOf(
                "timerFinishAt" to taskCategory.timerFinishAt,
            )

            firestore.collection(uid)
                .document(categoryId)
                .collection("tasks")
                .document(taskId)
                .update(data)
        }
    }
}