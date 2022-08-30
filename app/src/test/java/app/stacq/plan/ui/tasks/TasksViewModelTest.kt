package app.stacq.plan.ui.tasks

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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