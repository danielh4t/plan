package app.stacq.plan.ui.task

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.util.constants.FirebaseAnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


class TaskViewModel(
    private val taskRepository: TaskRepository,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    fun logPermission(isGranted: Boolean) {
        firebaseAnalytics.logEvent(FirebaseAnalyticsConstants.Event.NOTIFICATION_PERMISSION, bundleOf(
            "notifications" to if (isGranted) "true" else "false"
        ))
    }

    fun clone() {
        val name = task.value?.name
        val categoryId = task.value?.categoryId
        val goalId = task.value?.goalId
        var clonedTask: Task? = null
        if (name != null && categoryId != null) {
            val taskEntity = TaskEntity(name = name, categoryId = categoryId, goalId = goalId)
            clonedTask = taskEntity.asTask()
        }

        viewModelScope.launch {
            if (clonedTask != null) {
                // clone task
                taskRepository.create(clonedTask)
            }
        }
    }

    fun updatePriority(priority: Float) {
        val task: Task? = task.value
        task?.let {
            it.priority = priority.toInt()
            viewModelScope.launch {
                taskRepository.updatePriority(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}