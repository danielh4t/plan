package app.stacq.plan.ui.time

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.TaskTime
import app.stacq.plan.utility.TimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class TimeViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<TimeUIState> =
        MutableStateFlow(TimeUIState.Initial)
    val uiState: StateFlow<TimeUIState> =
        _uiState.asStateFlow()

    init {
        observeTask()
    }

    private fun observeTask() {
        viewModelScope.launch {
            taskRepository.getTasks()
                .onStart {
                    _uiState.value = TimeUIState.Loading
                }
                .catch { exception ->
                    _uiState.value = TimeUIState.Error(exception.message ?: "Error")
                }
                .collect { tasks ->

                    val formattedTasks = tasks.map { task ->
                        formatTaskToTaskTime(task)
                    }
                    val groupedTasks = formattedTasks.groupBy { task ->
                        TimeFormatter.formatDate(task.createdAt)
                    }
                    _uiState.value = TimeUIState.Success(groupedTasks)
                }
        }
    }
}

fun formatTaskToTaskTime(task: Task): TaskTime {
    return TaskTime(
        id = task.id,
        name = task.name,
        createdAt = task.createdAt,
        time = TimeFormatter.formatTaskTime(task.createdAt, task.completedAt),
    )
}

sealed interface TimeUIState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : TimeUIState

    /**
     * Still loading
     */
    data object Loading : TimeUIState

    /**
     * User is authenticated has been generated
     */
    data class Success(
        val groupedTasks: Map<String, List<TaskTime>>,
    ) : TimeUIState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : TimeUIState
}
