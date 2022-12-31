package app.stacq.plan.worker

import android.content.Context
import android.os.Bundle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.domain.asDocument
import app.stacq.plan.util.constants.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class SyncCategoryWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext
    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        return try {
            for (categoryEntity in categoryRepository.getCategoriesEntityList()) {
                categoryRepository.sync(categoryEntity.asDocument())
            }
            Result.success()
        } catch (throwable: Throwable) {
            val params = Bundle()
            params.putString("exception", throwable.message)
            firebaseAnalytics.logEvent(AnalyticsConstants.Event.SYNC_CATEGORY, params)
            Result.failure()
        }
    }
}