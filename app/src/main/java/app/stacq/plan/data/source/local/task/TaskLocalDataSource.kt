package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData

interface TaskLocalDataSource {

    suspend fun create(taskEntity: TaskEntity)

    suspend fun read(taskId: String): TaskEntity?

    suspend fun update(taskEntity: TaskEntity)

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun archive(taskId: String)

    suspend fun unarchive(taskId: String)

    suspend fun updateCompletion(taskEntity: TaskEntity)

    suspend fun updateTimerFinish(taskEntity: TaskEntity)

    suspend fun updateTimerAlarmById(taskId: String)

    suspend fun updatePriority(taskEntity: TaskEntity)

    suspend fun getTasksList(): List<TaskEntity>

    suspend fun hasGeneratedTask(goalId: String): Boolean

    suspend fun hasCompletedTaskGoalToday(goalId: String): Boolean

    fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>>

    fun getTask(taskId: String): LiveData<TaskEntityAndCategoryEntity>
}