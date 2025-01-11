package app.stacq.plan.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone


class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<TaskUIState> =
        MutableStateFlow(TaskUIState.Initial)
    val uiState: StateFlow<TaskUIState> =
        _uiState.asStateFlow()

    init {
        observeTask()
    }

    private fun observeTask() {
        viewModelScope.launch {
            val profileImageUrl = getProfileImageUrl()
            taskRepository.getTask()
                .onStart {
                    _uiState.value = TaskUIState.Loading
                }
                .catch { exception ->
                    _uiState.value = TaskUIState.Error(exception.message ?: "Error")
                }
                .collect { task ->
                    _uiState.value = TaskUIState.Success(profileImageUrl, false, task)
                }
        }
    }

    private fun getProfileImageUrl(): String {
        val user = Firebase.auth.currentUser
        return user?.photoUrl.toString()
    }


    fun showBottomSheet() {
        val currentState = _uiState.value
        if (currentState is TaskUIState.Success) {
            _uiState.value = currentState.copy(showBottomSheet = true)
        }
    }

    fun dismissBottomSheet() {
        val currentState = _uiState.value
        if (currentState is TaskUIState.Success) {
            _uiState.value = currentState.copy(showBottomSheet = false)
        }
    }

    /**
     * Saves tasks and fetches updated data
     * Dismisses Bottom Sheet
     */
    fun saveTask(name: String, notes: String?) {
        val currentState = _uiState.value
        val task = (currentState as TaskUIState.Success).task
        // check task name is not empty
        if (name.isNotBlank()) {
            // check if has current task
            if (task == null) {  // create
                val createdTask = Task(
                    name = name,
                    notes = if (notes.isNullOrEmpty()) null else notes
                )
                viewModelScope.launch {
                    try {
                        taskRepository.create(createdTask)
                        observeTask()
                    } catch (e: Exception) {
                        _uiState.value = TaskUIState.Error(e.message ?: "Error on creating")
                    }
                }
            } else { // update
                val updatedTask = task.copy(
                    name = name,
                    notes = if (notes.isNullOrEmpty()) null else notes
                )
                viewModelScope.launch {
                    try {
                        taskRepository.update(updatedTask)
                        observeTask()
                    } catch (e: Exception) {
                        _uiState.value = TaskUIState.Error(e.message ?: "Error on saving")
                    }
                }
            }
        }
    }

    fun complete(task: Task) {
        viewModelScope.launch {
            try {
                task.completedAt = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
                taskRepository.update(task)
            } catch (e: Exception) {
                _uiState.value = TaskUIState.Error(e.message ?: "Error on completing")
            }
        }
    }
}


sealed interface TaskUIState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : TaskUIState

    /**
     * Still loading
     */
    data object Loading : TaskUIState

    /**
     * User is authenticated has been generated
     */
    data class Success(
        val profileImageUrl: String?,
        val showBottomSheet: Boolean,
        val task: Task?,
    ) : TaskUIState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : TaskUIState
}
