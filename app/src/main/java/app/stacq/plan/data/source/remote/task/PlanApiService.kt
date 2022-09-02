package app.stacq.plan.data.source.remote.task

import app.stacq.plan.data.model.Task
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface PlanApiService {

    @GET("/tasks/")
    suspend fun getTasks(): List<Task>

    @PUT("/tasks/")
    suspend fun createTask(@Body task: Task)

}