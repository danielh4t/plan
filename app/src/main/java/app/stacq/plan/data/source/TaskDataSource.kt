package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

interface TaskDataSource {

    suspend fun getTasks(): LiveData<List<Task>>

    suspend fun create(task: Task)

    suspend fun readById(id: String): LiveData<Task>

    suspend fun update(task: Task)

    suspend fun deleteById(id: String)

    suspend fun updateCompletionById(id: String, completed: Boolean, completedAt: Long)

    suspend fun updateTimerFinishById(id: String, finishAt: Long)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePositionById(id: String, positionAt: Long)

    suspend fun getTasksCategory(): LiveData<List<TaskCategory>>

    suspend fun getTaskCategoryById(id: String): LiveData<TaskCategory>

}