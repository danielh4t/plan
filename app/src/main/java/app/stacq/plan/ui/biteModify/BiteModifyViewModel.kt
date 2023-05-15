package app.stacq.plan.ui.biteModify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.Task
import app.stacq.plan.domain.asBite
import kotlinx.coroutines.launch

class BiteModifyViewModel(
    private val biteRepository: BiteRepository,
    taskRepository: TaskRepository,
    taskId: String,
    biteId: String?
) : ViewModel() {

    val bite: LiveData<Bite> = if (biteId != null) {
        biteRepository.getBite(biteId)
    } else {
        MutableLiveData()
    }

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    fun create(name: String) {
        viewModelScope.launch {
            task.value?.let { task ->
                val biteEntity = BiteEntity(name = name, taskId = task.id, categoryId = task.categoryId)
                val bite = biteEntity.asBite()
                biteRepository.create(bite)
            }
        }
    }

    fun update(name: String) {
        viewModelScope.launch {
            bite.value?.let {
                it.name = name
                biteRepository.update(it)
            }
        }
    }
}