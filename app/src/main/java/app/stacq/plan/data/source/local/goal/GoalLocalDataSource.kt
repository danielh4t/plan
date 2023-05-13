package app.stacq.plan.data.source.local.goal

import androidx.lifecycle.LiveData

interface GoalLocalDataSource {

    suspend fun create(goalEntity: GoalEntity)

    suspend fun read(goalId: String): GoalEntity?

    suspend fun update(goalEntity: GoalEntity)

    suspend fun delete(goalEntity: GoalEntity)

    suspend fun upsert(goalEntity: GoalEntity)

    suspend fun getGoalEntities(): List<GoalEntity>

    suspend fun getCountGoalCompletedDays(goalId: String): Int

    fun getCount(): LiveData<Int>

    fun getGoals(): LiveData<List<GoalEntityAndCategoryEntity>>

    fun getGoal(goalId: String): LiveData<GoalEntityAndCategoryEntity>

    fun getActiveGoals(): LiveData<List<GoalEntityAndCategoryEntity>>

    fun getGenerateGoals(): List<GoalEntity>
}