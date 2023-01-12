package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.repository.BiteRepository


class SyncBiteWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

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
            Result.failure()
        }
    }
}