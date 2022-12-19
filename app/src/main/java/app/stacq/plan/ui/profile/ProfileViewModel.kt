package app.stacq.plan.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.work.*
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.util.yearDays
import app.stacq.plan.util.yearStartAt
import app.stacq.plan.worker.SyncBiteWorker
import app.stacq.plan.worker.SyncCategoryWorker
import app.stacq.plan.worker.SyncTaskWorker


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    internal val outputWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(WorkerConstants.TAGS.TASKS)

    private val database = PlanDatabase.getDatabase(application)
    private val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
    private val taskRemoteDataSource = TaskRemoteDataSource()
    private val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

    val days: Int = yearDays()

    val taskAnalysis: LiveData<List<TaskAnalysis>> = liveData {
        emitSource(taskRepository.getTaskAnalysis(yearStartAt()))
    }

    fun sync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncCategory = OneTimeWorkRequestBuilder<SyncCategoryWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.CATEGORIES)
            .build()

        val syncTask = OneTimeWorkRequestBuilder<SyncTaskWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.TASKS)
            .build()

        val syncBite = OneTimeWorkRequestBuilder<SyncBiteWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.BITES)
            .build()

        workManager.beginUniqueWork(
            WorkerConstants.UNIQUE.TASKS,
            ExistingWorkPolicy.KEEP,
            syncCategory
        )
            .then(syncTask)
            .then(syncBite)
            .enqueue()
    }
}