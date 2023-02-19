package app.stacq.plan.data.source.repository

import app.stacq.plan.data.source.FakeTaskDataSource
import app.stacq.plan.data.source.local.task.TaskEntity
import kotlinx.coroutines.Dispatchers
import org.junit.Before

class TaskRepositoryTest {

    private val codeEntity = TaskEntity(name = "Code", categoryId = "1")
    private val hackEntity = TaskEntity(name = "Hack", categoryId = "2")
    private val lifeEntity = TaskEntity(name = "Life", categoryId = "3")
    private val workEntity = TaskEntity(name = "Work", categoryId = "4")

    private val localTasks = listOf(codeEntity, hackEntity)
    private val remoteTasks = listOf(lifeEntity, workEntity)

    private lateinit var taskLocalDataSource: FakeTaskDataSource
    private lateinit var taskRemoteDataSource: FakeTaskDataSource

    private lateinit var taskRepository: TaskRepository

    @Before
    fun createRepository() {
        taskLocalDataSource = FakeTaskDataSource(localTasks.toMutableList())
        taskRemoteDataSource = FakeTaskDataSource(remoteTasks.toMutableList())
        taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource, Dispatchers.Unconfined)
    }

}