@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository


class GoalViewModelFactory(
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val goalId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(GoalViewModel::class.java) ->
                    return GoalViewModel(goalRepository, taskRepository,goalId) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}