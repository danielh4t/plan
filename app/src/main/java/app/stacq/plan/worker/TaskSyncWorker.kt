package app.stacq.plan.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.domain.asTask
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.map


class TaskSyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource =
            TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)

        return try {
            taskRemoteDataSource.getTasks().map {
                val taskEntity = it.asTask().asTaskEntity()
                // Ignore completed task
                it.completedAt.let { completedAt ->
                    if (completedAt == null || completedAt == 0L) {
                        taskLocalDataSource.upsert(taskEntity)
                    }
                }
            }
            taskLocalDataSource.getTasks().map {
                it.map { taskEntity ->
                    val taskDocument = taskEntity.asTask().asTaskDocument()
                    taskRemoteDataSource.update(taskDocument)
                }
            }
            Result.success()
        } catch (_: Throwable) {
            Result.failure()
        }
    }

    companion object {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        fun sync() = OneTimeWorkRequestBuilder<TaskSyncWorker>()
            .setConstraints(constraints)
            .build()
    }
}