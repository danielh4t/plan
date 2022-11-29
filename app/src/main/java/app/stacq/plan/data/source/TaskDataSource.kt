package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.model.Task

interface TaskDataSource {

    suspend fun create(taskEntity: TaskEntity)

    suspend fun getById(id: String): LiveData<TaskEntity>

    suspend fun update(taskEntity: TaskEntity)

    suspend fun deleteById(id: String)

    suspend fun updateCompletion(taskEntity: TaskEntity)

    suspend fun updateTimerFinish(taskEntity: TaskEntity)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePositionById(id: String, positionAt: Long)

    suspend fun getTasks(): LiveData<List<Task>>

    suspend fun getTaskCategoryById(id: String): LiveData<Task>

}