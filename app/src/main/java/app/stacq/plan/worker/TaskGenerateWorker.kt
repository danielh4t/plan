package app.stacq.plan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.domain.asTask
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TaskGenerateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        val database = PlanDatabase.getDatabase(appContext)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        return try {
            goalLocalDataSource.getGenerateGoals().map {
                val task = TaskEntity(name=it.name, categoryId = it.categoryId, goalId = it.id)
                taskRepository.create(task.asTask())
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}