@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.goalModify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository


class GoalModifyViewModelFactory(
    private val goalRepository: GoalRepository,
    private val categoryRepository: CategoryRepository,
    private val goalId: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(GoalModifyViewModel::class.java) ->
                    return GoalModifyViewModel(goalRepository, categoryRepository, goalId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}