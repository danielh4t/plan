package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.domain.asCategory
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
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

        return try {
            categoryLocalDataSource.getCategoryEntities().map {
                val categoryDocument = it.asCategory().asDocument()
                categoryRemoteDataSource.update(categoryDocument)
            }

            categoryRemoteDataSource.getCategoryDocuments().map {
                val categoryEntity = it.asCategory().asEntity()
                // Ignore deleted categories
                if(!it.deleted) {
                    categoryLocalDataSource.upsert(categoryEntity)
                }
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}