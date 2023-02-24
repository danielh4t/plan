package app.stacq.plan.data.repository.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.stacq.plan.domain.Task
import kotlinx.coroutines.runBlocking

class FakeTaskRepository: TaskRepository {

    var tasksList = mutableListOf<Task>()

    private val tasks = MutableLiveData<List<Task>>()
    override suspend fun create(task: Task) {
        tasksList.add(task)
        runBlocking { tasks.value = tasksList.toList() }
    }

    override suspend fun read(id: String): Task? {
        return tasksList.find { task -> task.id === id}
    }

    override suspend fun update(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(task: Task) {
        tasksList.remove(task)
        runBlocking { tasks.value = tasksList.toList() }
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
        return tasks
    }

    override fun getTask(id: String): LiveData<Task> {
        TODO("Not yet implemented")
    }
}