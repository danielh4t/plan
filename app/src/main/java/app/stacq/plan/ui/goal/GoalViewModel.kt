package app.stacq.plan.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.domain.Goal
import app.stacq.plan.domain.asTask
import kotlinx.coroutines.launch


class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    goalId: String
) : ViewModel() {

    val goal: LiveData<Goal> = goalRepository.getGoal(goalId)

    val completedDays: LiveData<Int> = goalRepository.getCountGoalCompletedDays(goalId)

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

    fun generate() {
        val goal: Goal? = goal.value
        goal?.let {
            val task = TaskEntity(name=it.name, categoryId = it.categoryId, goalId = it.id)
            viewModelScope.launch {
                taskRepository.create(task.asTask())
            }
        }
    }
}