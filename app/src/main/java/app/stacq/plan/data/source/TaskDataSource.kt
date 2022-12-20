package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity

interface TaskDataSource {

    suspend fun create(taskEntity: TaskEntity)

    fun getById(id: String): LiveData<TaskEntity>

    suspend fun update(taskEntity: TaskEntity)

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun updateCompletion(taskEntity: TaskEntity)

    suspend fun updateTimerFinish(taskEntity: TaskEntity)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePriority(taskEntity: TaskEntity)

    suspend fun getTasksList(): List<TaskEntity>

    fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>

    fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity>

    fun getTaskAnalysis(yearStartAt: Long): LiveData<List<TaskAnalysis>>
}