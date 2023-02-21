@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.createBite


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl


class CreateBiteViewModelFactory(
    private val biteRepositoryImpl: BiteRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(CreateBiteViewModel::class.java) ->
                    return CreateBiteViewModel(biteRepositoryImpl) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}