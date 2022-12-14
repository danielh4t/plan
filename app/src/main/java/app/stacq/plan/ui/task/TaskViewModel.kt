package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asTask
import kotlinx.coroutines.launch
import java.time.Instant


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val bitesRepository: BiteRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val bites: LiveData<List<Bite>> = liveData {
        emitSource(bitesRepository.getBites(taskId))
    }

    fun clone() {
        val name = task.value?.name
        val categoryId = task.value?.categoryId
        viewModelScope.launch {
            if (name != null && categoryId != null) {
                val taskEntity = TaskEntity(name = name, categoryId = categoryId)
                val task = taskEntity.asTask()
                taskRepository.create(task)
            }
        }
    }

    fun delete() {
        val task: Task? = task.value
        task?.let {
            viewModelScope.launch {
                taskRepository.delete(it)
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