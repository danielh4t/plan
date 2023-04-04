package app.stacq.plan.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Goal
import app.stacq.plan.util.time.TimeUtil
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val goals: LiveData<List<Goal>> = goalRepository.getGoals()

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun complete(goal: Goal) {
        viewModelScope.launch {
            goal.completed = !goal.completed
            goal.completedAt = if(goal.completed) {
                TimeUtil().nowInSeconds()
            } else {
                0L
            }
            goalRepository.update(goal)
        }
    }
}