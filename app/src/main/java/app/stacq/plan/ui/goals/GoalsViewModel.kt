package app.stacq.plan.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Goal
import kotlinx.coroutines.launch
import java.time.Instant


class GoalsViewModel(
    private val goalRepository: GoalRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val goals: LiveData<List<Goal>> = goalRepository.getGoals()

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun complete(goal: Goal) {
        viewModelScope.launch {
            goal.completed = !goal.completed
            goal.completedAt = Instant.now().epochSecond
            goalRepository.update(goal)
        }
    }
}