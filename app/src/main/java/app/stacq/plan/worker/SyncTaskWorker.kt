package app.stacq.plan.worker

import android.content.Context
import android.os.Bundle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class SyncTaskWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext
    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    
    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

        return try {
            for (taskEntity in taskRepository.geTasksList()) {
                taskRepository.syncTaskEntity(taskEntity)
            }
            Result.success()
        } catch (throwable: Throwable) {
            val params = Bundle()
            params.putString("exception", throwable.message)
            firebaseAnalytics.logEvent(AnalyticsConstants.Event.SYNC_TASK, params)
            Result.failure()
        }

    }
}