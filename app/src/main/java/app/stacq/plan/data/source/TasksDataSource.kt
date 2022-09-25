package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

interface TasksDataSource {

    suspend fun getTasks(): LiveData<List<Task>>

    suspend fun createTask(task: Task)

    suspend fun readTaskById(id: String): LiveData<Task>

    suspend fun updateTask(task: Task)

    suspend fun deleteById(id: String)

    suspend fun updateTaskCompletionById(id: String)

    suspend fun updateTaskTimerFinishById(id: String, finishAt: Long)

    suspend fun updateTaskTimerAlarmById(id: String)

    suspend fun getTasksCategory(): LiveData<List<TaskCategory>>

    suspend fun readTaskCategoryById(id: String): LiveData<TaskCategory>

}