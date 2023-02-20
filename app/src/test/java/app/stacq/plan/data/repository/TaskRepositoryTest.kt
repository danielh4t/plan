package app.stacq.plan.data.repository


import app.stacq.plan.data.source.local.FakeTaskLocalDataSource
import app.stacq.plan.data.source.remote.FakeTaskRemoteDataSource
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTaskDocument
import app.stacq.plan.domain.asTaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    private val task = Task(name = "Task", categoryId = "1")
    private val localTasks = listOf(task.asTaskEntity())
    private val remoteTasks = listOf(task.asTaskDocument())

    private lateinit var taskLocalDataSource: FakeTaskLocalDataSource
    private lateinit var taskRemoteDataSource: FakeTaskRemoteDataSource

    private lateinit var taskRepository: TaskRepository

    @Before
    fun createRepository() {
        taskLocalDataSource = FakeTaskLocalDataSource(localTasks.toMutableList())
        taskRemoteDataSource = FakeTaskRemoteDataSource(remoteTasks.toMutableList())
        taskRepository =
            TaskRepository(taskLocalDataSource, taskRemoteDataSource, Dispatchers.Unconfined)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateTaskPriority_priorityChanged_taskPriorityUpdatedOnLocalDataSourceAndRemoteDataSource() =
        runTest(UnconfinedTestDispatcher()) {
            // Given a task to persist
            val priority = 10
            task.priority = priority
            // When a task priority is updated from the task repository
            launch { taskRepository.updatePriority(task) }

            // Then the task priority is updated
            assertThat(taskRepository.read(task.id)?.priority, IsEqual(priority))
        }
}