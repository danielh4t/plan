package app.stacq.plan.ui.biteModify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.asBite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BiteModifyViewModel(
    private val biteRepository: BiteRepository,
    private val taskRepository: TaskRepository,
    private val taskId: String,
    biteId: String?
) : ViewModel() {

    val bite: LiveData<Bite> = if (biteId != null) {
        biteRepository.getBite(biteId)
    } else {
        MutableLiveData()
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    fun create(name: String) {
        scope.launch {
            val task = taskRepository.read(taskId)
            task?.let {
                val biteEntity =
                    BiteEntity(name = name, taskId = it.id, categoryId = it.categoryId)
                val bite = biteEntity.asBite()
                biteRepository.create(bite)
            }
        }
    }

    fun update(name: String) {
        scope.launch {
            bite.value?.let {
                it.name = name
                biteRepository.update(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}