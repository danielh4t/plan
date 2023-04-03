package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.source.local.PlanDatabase
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

        return try {
            goalLocalDataSource.getGoalEntities().map {
                val goalDocument = it.asGoal().asDocument()
                goalRemoteDataSource.update(goalDocument)
            }

            goalRemoteDataSource.getGoalDocuments().map {
                val goalEntity = it.asGoal().asEntity()
                goalLocalDataSource.upsert(goalEntity)
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}