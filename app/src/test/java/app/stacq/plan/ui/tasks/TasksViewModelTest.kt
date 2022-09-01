package app.stacq.plan.ui.tasks


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.stacq.plan.data.source.repository.TasksRepository

import org.junit.Before
import org.junit.Rule

import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock


@RunWith(MockitoJUnitRunner::class)
class TasksViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TasksRepository
    private lateinit var viewModel: TasksViewModel

    @Before
    fun before() {
        repository = mock()
        viewModel = TasksViewModel(repository)
    }


}