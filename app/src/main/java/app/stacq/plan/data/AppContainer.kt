package app.stacq.plan.data

import android.content.Context
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val taskRepository: TaskRepository
}

/**
 * [AppContainer] implementation that provides instance of [TaskRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [TaskRepository]
     */
    override val taskRepository: TaskRepository by lazy {
        val local = TaskLocalDataSourceImpl(PlanDatabase.getDatabase(context).taskDao())
        val remote = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        TaskRepositoryImpl(local, remote)
    }
}
