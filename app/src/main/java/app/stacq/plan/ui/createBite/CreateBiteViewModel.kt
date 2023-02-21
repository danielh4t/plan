package app.stacq.plan.ui.createBite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.domain.asBite
import kotlinx.coroutines.launch

class CreateBiteViewModel(
    private val biteRepositoryImpl: BiteRepositoryImpl,
) : ViewModel() {

    fun create(name: String, taskId: String) {
        viewModelScope.launch {
            val biteEntity = BiteEntity(name = name, taskId = taskId)
            val bite = biteEntity.asBite()
            biteRepositoryImpl.create(bite)
        }
    }
}