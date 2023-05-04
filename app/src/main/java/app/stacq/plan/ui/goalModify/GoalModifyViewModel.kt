package app.stacq.plan.ui.goalModify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.source.local.goal.GoalEntity
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Goal
import app.stacq.plan.domain.asGoal
import kotlinx.coroutines.launch

class GoalModifyViewModel(
    private val goalRepository: GoalRepository,
    categoryRepository: CategoryRepository,
    goalId: String?
) : ViewModel() {

    val goal: LiveData<Goal> = if (goalId != null) {
        goalRepository.getGoal(goalId)
    } else {
        MutableLiveData()
    }

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun create(
        name: String,
        measure: String,
        result: String,
        categoryId: String,
        days: Int
    ): String {
        val goalEntity = GoalEntity(
            name = name,
            measure = measure,
            result = result,
            categoryId = categoryId,
            days = days
        )
        viewModelScope.launch {
            val goal = goalEntity.asGoal()
            goalRepository.create(goal)
        }
        return goalEntity.id
    }

    fun update(name: String, measure: String, result: String, categoryId: String, days: Int) {
        viewModelScope.launch {
            goal.value?.let {
                it.name = name
                it.measure = measure
                it.result = result
                it.days = days
                if (it.categoryId == categoryId) {
                    goalRepository.update(it)
                } else {
                    // update category
                    val previousCategoryId = it.categoryId
                    it.categoryId = categoryId
                    goalRepository.updateCategory(it, previousCategoryId)
                }
            }
        }
    }
}