package app.stacq.plan.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.domain.Goal
import kotlinx.coroutines.launch
import java.time.Instant


class GoalsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    val goals: LiveData<List<Goal>> = goalRepository.getGoals()

    fun complete(goal: Goal) {
        viewModelScope.launch {
            goal.completed = !goal.completed
            goal.completedAt = Instant.now().epochSecond
            goalRepository.update(goal)
        }
    }
}