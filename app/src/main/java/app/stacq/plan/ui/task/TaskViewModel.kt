package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asBite
import app.stacq.plan.domain.asTask
import app.stacq.plan.util.constants.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.Instant


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val bitesRepository: BiteRepository,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val bites: LiveData<List<Bite>> = bitesRepository.getBites(taskId)

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logPermission(isGranted: Boolean) {
        firebaseAnalytics.logEvent(AnalyticsConstants.Event.APP_PERMISSION) {
            param("notifications", if (isGranted) "true" else "false")
        }
    }
    fun clone() {
        val name = task.value?.name
        val categoryId = task.value?.categoryId
        val goalId = task.value?.goalId
        val bites = bites.value
        viewModelScope.launch {
            if (name != null && categoryId != null && bites != null) {
                val taskEntity = TaskEntity(name = name, categoryId = categoryId, goalId = goalId)
                val task = taskEntity.asTask()
                taskRepository.create(task)
                // clone incomplete bites
                bites.filter { bite -> !bite.completed }.forEach {
                    val biteEntity = BiteEntity(name = it.name, taskId = task.id, categoryId = categoryId)
                    bitesRepository.create(biteEntity.asBite())
                }
            }
        }
    }

    fun delete() {
        val task: Task? = task.value
        val bites: List<Bite>? = bites.value
        task?.let {
            viewModelScope.launch {
                taskRepository.delete(it)
                bites?.let {
                    it.forEach { bite ->
                        bitesRepository.delete(bite)
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
        bite.completedAt = Instant.now().epochSecond
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
            if (it.timerAlarm && it.timerFinishAt > Instant.now().epochSecond)
                return true
        }
        return false
    }
}