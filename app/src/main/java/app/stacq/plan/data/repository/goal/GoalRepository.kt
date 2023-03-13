package app.stacq.plan.data.repository.goal

import androidx.lifecycle.LiveData
import app.stacq.plan.domain.Goal


interface GoalRepository {
    suspend fun create(goal: Goal)

    suspend fun read(goalId: String): Goal?

    suspend fun update(goal: Goal)

    suspend fun delete(goal: Goal)

    suspend fun updateCategory(goal: Goal, previousCategoryId: String)

    fun getCountGoalCompletedDays(goalId: String): LiveData<Int>

    fun getGoals(): LiveData<List<Goal>>

    fun getGoal(goalId: String): LiveData<Goal>

    fun getActiveGoals(): LiveData<List<Goal>>
}