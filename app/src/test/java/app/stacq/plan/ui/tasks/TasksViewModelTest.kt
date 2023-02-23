package app.stacq.plan.ui.tasks


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.stacq.plan.data.repository.category.FakeCategoryRepository
import app.stacq.plan.data.repository.task.FakeTaskRepository
import org.junit.Before
import org.junit.Rule


class TasksViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var viewModel: TasksViewModel

    @Before
    fun setup() {
        taskRepository = FakeTaskRepository()
        categoryRepository = FakeCategoryRepository()

        viewModel = TasksViewModel(taskRepository, categoryRepository)
    }
}