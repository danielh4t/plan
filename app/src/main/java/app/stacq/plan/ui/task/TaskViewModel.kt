package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import app.stacq.plan.util.constants.AnalyticsConstants
import app.stacq.plan.util.time.TimeUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val bitesRepository: BiteRepository,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val bites: LiveData<List<Bite>> = bitesRepository.getBites(taskId)

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logPermission(isGranted: Boolean) {
        firebaseAnalytics.logEvent(AnalyticsConstants.Event.NOTIFICATION_PERMISSION) {
            param("notifications", if (isGranted) "true" else "false")
        }
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

        val bites = bites.value
        viewModelScope.launch {
            if (clonedTask != null) {
                // clone task
                taskRepository.create(clonedTask)
                if (bites != null) {
                    // move incomplete bites
                    val incompleteBites = bites.filter { !it.completed }.map {
                        it.copy(taskId = clonedTask.id)
                    }
                    incompleteBites.map {
                        bitesRepository.update(it)
                    }
                }
            }
        }
    }

    fun delete() {
        val task: Task? = task.value
        val bites: List<Bite>? = bites.value
        task?.let {
            if (task.goalId != null) {
                viewModelScope.launch {
                    taskRepository.archive(task.id)
                }
            } else {
                viewModelScope.launch {
                    taskRepository.delete(task)
                    bites?.let {
                        it.forEach { bite ->
                            bitesRepository.delete(bite)
                        }
                    }
                }
            }
        }
    }

    fun undoDelete() {
        val task: Task? = task.value
        val bites: List<Bite>? = bites.value
        task?.let {
            viewModelScope.launch {
                taskRepository.create(it)
                bites?.let {
                    it.forEach { bite ->
                        bitesRepository.create(bite)
                    }
                }
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

    fun completeBite(bite: Bite) {
        bite.completed = !bite.completed
        bite.completedAt = TimeUtil().nowInSeconds()
        viewModelScope.launch {
            bitesRepository.update(bite)
        }
    }

    fun deleteBite(bite: Bite) {
        viewModelScope.launch {
            bitesRepository.delete(bite)
        }
    }

    fun hasAlarm(): Boolean {
        val task: Task? = task.value
        task?.let {
            if (it.timerAlarm && it.timerFinishAt > TimeUtil().nowInSeconds())
                return true
        }
        return false
    }
}