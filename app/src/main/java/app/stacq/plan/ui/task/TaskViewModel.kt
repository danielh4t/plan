package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.model.Bite
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.model.asTask
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.Instant


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val bitesRepository: BiteRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = liveData {
        emitSource(taskRepository.getTask(taskId))
    }

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

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }
    }

    fun completeBite(bite: Bite) {
        viewModelScope.launch {
            bite.completed = !bite.completed
            bite.completedAt = Instant.now().epochSecond
            bitesRepository.update(bite)
        }
    }

}
