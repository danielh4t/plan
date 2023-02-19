package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity
import app.stacq.plan.domain.Task

interface TaskDataSource {

    suspend fun create(task: Task)

    fun getById(id: String): LiveData<Task>

    suspend fun update(task: Task)

    suspend fun delete(task: Task)

    suspend fun updateCompletion(task: Task)

    suspend fun updateTimerFinish(task: Task)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePriority(task: Task)

    suspend fun getTasksList(): List<Task>

    fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>

    fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity>

    fun getTaskAnalysis(yearStartAt: Long): LiveData<List<TaskAnalysis>>
}