package app.stacq.plan.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.domain.Goal
import kotlinx.coroutines.launch


class GoalViewModel(
    private val goalRepository: GoalRepository,
    goalId: String
) : ViewModel() {

    val goal: LiveData<Goal> = goalRepository.getGoal(goalId)

    fun delete() {
        val goal: Goal? = goal.value
        goal?.let {
            viewModelScope.launch {
                goalRepository.delete(it)
            }
        }
    }

    fun undoDelete() {
        val goal: Goal? = goal.value
        goal?.let {
            viewModelScope.launch {
                goalRepository.create(it)
            }
        }
    }

    fun updateGenerate(isChecked: Boolean) {
        val goal: Goal? = goal.value
        goal?.let {
            viewModelScope.launch {
                if(it.generate != isChecked) {
                    it.generate = !it.generate
                    goalRepository.update(it)
                }
            }
        }
    }
}