package app.stacq.plan.data.source.local

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity


class FakeTaskLocalDataSource(private val tasks: MutableList<TaskEntity>? = mutableListOf()) :
    TaskLocalDataSource {

    override suspend fun create(taskEntity: TaskEntity) {
        tasks?.add(taskEntity)
    }

    override suspend fun read(id: String): TaskEntity? {
        return tasks?.find { task -> task.id === id}
    }

    override suspend fun update(taskEntity: TaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(taskEntity: TaskEntity) {
        tasks?.remove(taskEntity)
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
        tasks?.find { task -> task.id === taskEntity.id}?.priority = taskEntity.priority
    }

    override suspend fun getTasksList(): List<TaskEntity> {
        TODO("Not yet implemented")
    }

    override fun getTasks(): LiveData<List<TaskEntityAndCategoryEntity>> {
        TODO("Not yet implemented")
    }

    override fun getTask(id: String): LiveData<TaskEntityAndCategoryEntity> {
        TODO("Not yet implemented")
    }

    override fun getTaskAnalysis(yearStartAt: Long): LiveData<List<Int>> {
        TODO("Not yet implemented")
    }
}