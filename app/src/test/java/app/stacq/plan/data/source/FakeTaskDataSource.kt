package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.task.TaskAnalysis
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity


class FakeTaskDataSource(private val tasks: MutableList<TaskEntity>? = mutableListOf()): TaskDataSource {

    override suspend fun create(taskEntity: TaskEntity) {
        tasks?.add(taskEntity)
    }

    override fun getById(id: String): LiveData<TaskEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun update(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompletion(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTimerFinish(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTimerAlarmById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePriority(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksList(): List<TaskEntity> {
        TODO("Not yet implemented")
    }

    override fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>> {
        TODO("Not yet implemented")
    }

    override fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity> {
        TODO("Not yet implemented")
    }

    override fun getTaskAnalysis(yearStartAt: Long): LiveData<List<TaskAnalysis>> {
        TODO("Not yet implemented")
    }
}