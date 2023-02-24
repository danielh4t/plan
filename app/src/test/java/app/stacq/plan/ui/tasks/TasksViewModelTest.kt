package app.stacq.plan.ui.tasks


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.stacq.plan.data.repository.category.FakeCategoryRepository
import app.stacq.plan.data.repository.task.FakeTaskRepository
import app.stacq.plan.domain.Task
import app.stacq.plan.helpers.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TasksViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var viewModel: TasksViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        taskRepository = FakeTaskRepository()
        categoryRepository = FakeCategoryRepository()

        viewModel = TasksViewModel(taskRepository, categoryRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun singleTask_deleteTask_emptyTasksOnLocalDataSource() =
        runTest(UnconfinedTestDispatcher()) {
            // Given a task to delete
            val task = Task(name = "Task", categoryId = "1")
            launch { taskRepository.create(task) }

            // When a task is delete
            viewModel.delete(task)

            // Then the task is delete on local data source

            assertThat(taskRepository.getTasks().getOrAwaitValue(), IsEqual(emptyList()))
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doubleTasks_deleteTask_singleTaskDeletedOnLocalDataSource() =
        runTest(UnconfinedTestDispatcher()) {
            // Given a task to delete
            val taskOne = Task(name = "Task", categoryId = "1")
            val taskTwo = Task(name = "Task", categoryId = "2")
            launch { taskRepository.create(taskOne) }
            launch { taskRepository.create(taskTwo) }

            // When a task is delete
            viewModel.delete(taskOne)

            // Then the task is delete on local data source
            assertThat(taskRepository.getTasks().getOrAwaitValue(), IsEqual(listOf(taskTwo)))
        }
}