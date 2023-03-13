@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.biteModify


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.task.TaskRepository


class BiteModifyViewModelFactory(
    private val biteRepository: BiteRepository,
    private val taskRepository: TaskRepository,
    private val taskId: String,
    private val biteId: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(BiteModifyViewModel::class.java) ->
                    return BiteModifyViewModel(biteRepository, taskRepository, taskId, biteId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}