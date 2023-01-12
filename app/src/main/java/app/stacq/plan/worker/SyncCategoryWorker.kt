package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.domain.asCategory
import app.stacq.plan.domain.asDocument


class SyncCategoryWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        return try {
            for (categoryEntity in categoryRepository.getCategoriesEntities()) {
                categoryRepository.syncRemote(categoryEntity.asDocument())
            }

            for (categoryDocument in categoryRepository.getCategoriesDocuments()) {
                categoryDocument?.let { categoryRepository.syncLocal(it.asCategory()) }
            }

            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}