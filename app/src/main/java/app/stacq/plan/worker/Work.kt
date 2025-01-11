package app.stacq.plan.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager


object Work {
    // This method is initializes work, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val continuation = workManager.beginUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            TaskSyncWorker.sync(),
        )
        continuation.enqueue()
    }
}

// This names should not be changed otherwise the app may have concurrent sync requests running
internal const val SYNC_WORK_NAME: String = "Sync"