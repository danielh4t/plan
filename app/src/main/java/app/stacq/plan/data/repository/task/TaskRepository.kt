package app.stacq.plan.data.repository.task

import androidx.lifecycle.LiveData
import app.stacq.plan.domain.Task

interface TaskRepository {
    suspend fun create(task: Task)

    suspend fun read(id: String): Task?

    suspend fun update(task: Task)

    suspend fun delete(task: Task)

    suspend fun updateCategory(task: Task, previousCategoryId: String)

    suspend fun updateCompletion(task: Task)

    suspend fun updateTimerFinish(task: Task)

    suspend fun updateTimerAlarmById(id: String)

    suspend fun updatePriority(task: Task)

    suspend fun getCategoryProfileCompleted(categoryId: String): MutableMap<String, Any>?

    fun getTasks(): LiveData<List<Task>>

    fun getTask(id: String): LiveData<Task>
}