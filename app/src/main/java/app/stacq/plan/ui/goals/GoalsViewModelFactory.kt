@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.goal.GoalRepository


class GoalsViewModelFactory(private val goalRepository: GoalRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(GoalsViewModel::class.java) ->
                    return GoalsViewModel(goalRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
