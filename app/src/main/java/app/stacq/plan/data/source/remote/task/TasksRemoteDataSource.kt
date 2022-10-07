package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TasksRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun createTask(task: Task) = withContext(ioDispatcher) {
        val taskMap = hashMapOf(
            "createdAt" to task.createdAt,
            "title" to task.title,
            "categoryId" to task.categoryId,
            "completed" to task.completed,
            "completedAt" to task.completedAt,
            "timerFinishAt" to task.timerFinishAt,
            "timerAlarm" to task.timerAlarm
        )
        firestore.collection("tasks").document(task.id).set(taskMap)
    }

    suspend fun updateTask(task: Task) = withContext(ioDispatcher) {
        val taskMap = hashMapOf(
            "createdAt" to task.createdAt,
            "title" to task.title,
            "categoryId" to task.categoryId,
            "completed" to task.completed,
            "completedAt" to task.completedAt,
            "timerFinishAt" to task.timerFinishAt,
            "timerAlarm" to task.timerAlarm
        )
        firestore.collection("tasks").document(task.id).set(taskMap)
    }

}