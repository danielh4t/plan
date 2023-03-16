package app.stacq.plan.data.source.local.goal

import androidx.lifecycle.LiveData

interface GoalLocalDataSource {

    suspend fun create(goalEntity: GoalEntity)

    suspend fun read(goalId: String): GoalEntity?

    suspend fun update(goalEntity: GoalEntity)

    suspend fun delete(goalEntity: GoalEntity)

    fun getCountGoalCompletedDays(goalId: String): LiveData<Int>

    fun getGoals(): LiveData<List<GoalEntityAndCategoryEntity>>

    fun getGoal(goalId: String): LiveData<GoalEntityAndCategoryEntity>

    fun getActiveGoals(): LiveData<List<GoalEntityAndCategoryEntity>>
}