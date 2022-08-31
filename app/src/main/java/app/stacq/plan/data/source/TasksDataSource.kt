package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

/**
 * Main entry point for accessing posts data.
 */
interface TasksDataSource {

    suspend fun getTaskAndCategoryName(): LiveData<List<TaskCategory>>

    suspend fun getTasks(): LiveData<List<Task>>

    suspend fun getTaskCategoryById(id: String): LiveData<TaskCategory>

    suspend fun getTaskById(id: String): LiveData<Task>

    suspend fun insert(task: Task)

    suspend fun update(task: Task)

    suspend fun delete(task: Task)

    suspend fun deleteById(id: String)

    suspend fun complete(id: String, isCompleted: Boolean, completedAt: Long)

}