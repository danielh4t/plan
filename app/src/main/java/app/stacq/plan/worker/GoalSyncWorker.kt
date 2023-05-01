package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import app.stacq.plan.domain.asGoal
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalSyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())

        return try {
            categoryLocalDataSource.getCategoryEntities().map { category ->
                if (category.enabled && !category.deleted) {
                    goalRemoteDataSource.getGoalDocuments(category.id).map {
                        val goalEntity = it.asGoal().asEntity()
                        it.deleted?.let { deleted ->
                            if (!deleted) {
                                goalLocalDataSource.upsert(goalEntity)
                            }
                        }
                    }
                }
            }

            goalLocalDataSource.getGoalEntities().map {
                val goalDocument = it.asGoal().asDocument()
                goalRemoteDataSource.update(goalDocument)
            }

            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}