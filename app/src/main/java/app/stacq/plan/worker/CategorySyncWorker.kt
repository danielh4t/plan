package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CategorySyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        return try {
            categoryRepository.syncCategories()
            Result.success()
        } catch (throwable: Throwable) {

            Result.failure()
        }
    }
}