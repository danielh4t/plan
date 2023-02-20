package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData

interface TaskLocalDataSource {

    suspend fun create(taskEntity: TaskEntity)

    suspend fun read(id: String): TaskEntity?

    suspend fun update(taskEntity: TaskEntity)

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun updateCompletion(taskEntity: TaskEntity)

    suspend fun updateTimerFinish(taskEntity: TaskEntity)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePriority(taskEntity: TaskEntity)

    suspend fun getTasksList(): List<TaskEntity>

    fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>>

    fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity>

    fun getTaskAnalysis(yearStartAt: Long): LiveData<List<Int>>
}