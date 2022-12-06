package app.stacq.plan.ui.profile

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.work.*
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.numberOfDays
import app.stacq.plan.util.startOfDay
import app.stacq.plan.worker.SyncBiteWorker
import app.stacq.plan.worker.SyncCategoryWorker
import app.stacq.plan.worker.SyncTaskWorker
import app.stacq.plan.util.constants.WorkerConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val workManager = WorkManager.getInstance(application)

    internal val outputWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(WorkerConstants.TAGS.TASKS)

    private val database = PlanDatabase.getDatabase(application)
    private val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
    private val taskRemoteDataSource = TaskRemoteDataSource()
    private val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

    val days: Int = numberOfDays()

    var currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)

    val taskAnalysis: LiveData<List<TaskAnalysis>> = liveData {
        emitSource(taskRepository.countCompletedInMonth(startOfDay(0)))
    }

    fun logAuthentication(errorCode: Int) {
        val params = Bundle()
        params.putInt("login_error_code", errorCode)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }

    fun sync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncCategory = OneTimeWorkRequestBuilder<SyncCategoryWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.CATEGORIES)
            .build()

        val syncTask = OneTimeWorkRequestBuilder<SyncTaskWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.TASKS)
            .build()

        val syncBite = OneTimeWorkRequestBuilder<SyncBiteWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.BITES)
            .build()

        workManager.beginUniqueWork(
            WorkerConstants.UNIQUE.TASKS,
            ExistingWorkPolicy.KEEP,
            syncCategory
        )
            .then(syncTask)
            .then(syncBite)
            .enqueue()
    }

    fun calculatePercentage(daysComplete: Int): String {
        val rate = daysComplete.toFloat() / days
        val percent = rate * 100L
        return String.format("%.1f", percent).plus("%")
    }

    fun cancelCleanDatabase() {
        TODO("Not yet implemented")
    }

}