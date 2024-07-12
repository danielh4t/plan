package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.domain.asGoal
import app.stacq.plan.util.TimeUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.max


class GoalProgressWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        return try {
            goalLocalDataSource.getGoalEntities().map {
                // update progress with maximum value to prevent overwrite of remote synced data
                it.progress = max(it.progress, goalLocalDataSource.getCountGoalCompletedDays(it.id))
                // update goal generate and completedAt when progress is same as goal days
                if (it.progress == it.days) {
                    it.generate = false
                    it.completedAt = TimeUtil().nowInSeconds()
                }
                goalRepository.update(it.asGoal())
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}