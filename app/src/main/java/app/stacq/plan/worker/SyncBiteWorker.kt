package app.stacq.plan.worker

import android.content.Context
import android.os.Bundle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class SyncBiteWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext
    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val biteLocalDataSource = BiteLocalDataSource(database.biteDao())
        val biteRemoteDataSource = BiteRemoteDataSource()
        val biteRepository = BiteRepository(biteLocalDataSource, biteRemoteDataSource)

        return try {
            for (biteEntity in biteRepository.getBitesList()) {
                biteRepository.sync(biteEntity)
            }
            Result.success()
        } catch (throwable: Throwable) {
            val params = Bundle()
            params.putString("exception", throwable.message)
            firebaseAnalytics.logEvent(AnalyticsConstants.Event.SYNC_BITE, params)
            Result.failure()
        }

    }
}