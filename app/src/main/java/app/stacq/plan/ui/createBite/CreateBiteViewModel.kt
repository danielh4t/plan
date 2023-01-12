package app.stacq.plan.ui.createBite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.domain.asBite
import kotlinx.coroutines.launch

class CreateBiteViewModel(
    private val biteRepository: BiteRepository,
) : ViewModel() {

    fun create(name: String, taskId: String) {
        viewModelScope.launch {
            val biteEntity = BiteEntity(name = name, taskId = taskId)
            val bite = biteEntity.asBite()
            biteRepository.create(bite)
        }
    }
}