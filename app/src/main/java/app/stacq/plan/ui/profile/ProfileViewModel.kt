package app.stacq.plan.ui.profile

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import app.stacq.plan.worker.SyncBiteWorker
import app.stacq.plan.worker.SyncCategoryWorker
import app.stacq.plan.worker.SyncTaskWorker
import app.stacq.plan.worker.WorkerConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private val workManager = WorkManager.getInstance(application)

    internal val outputWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(WorkerConstants.TAGS.TASKS)


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


}