package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import retrofit2.http.*

interface PlanApiService {

    @GET("/tasks/")
    suspend fun getTasks(): List<Task>

    @PUT("/tasks/")
    suspend fun createTask(@Body task: Task)

    @GET("/tasks/{taskId}")
    suspend fun readTaskById(@Path("taskId") id: String): Task

    @POST("/tasks/")
    suspend fun updateTask(@Body task: Task)

    @POST("/tasks/{taskId}")
    suspend fun updateTaskCompletionById(@Path("taskId") id: String)

    @DELETE("/tasks/{taskId}")
    suspend fun deleteTaskById(@Path("taskId") id: String)

    @GET("/tasksCategory/")
    suspend fun getTasksCategory(): List<TaskCategory>

    @GET("/tasksCategory/{taskId}")
    suspend fun readTaskCategoryById(@Path("taskId") id: String): List<TaskCategory>

}