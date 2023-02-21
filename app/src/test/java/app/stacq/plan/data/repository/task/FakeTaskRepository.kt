package app.stacq.plan.data.repository.task

import androidx.lifecycle.LiveData
import app.stacq.plan.domain.Task

class FakeTaskRepository: TaskRepository {
    override suspend fun create(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun read(id: String): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun update(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(task: Task, previousCategoryId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompletion(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTimerFinish(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTimerAlarmById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePriority(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryProfileCompleted(categoryId: String): MutableMap<String, Any>? {
        TODO("Not yet implemented")
    }

    override fun getTasks(): LiveData<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getTask(id: String): LiveData<Task> {
        TODO("Not yet implemented")
    }
}