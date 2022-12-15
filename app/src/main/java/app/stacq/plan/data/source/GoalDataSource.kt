package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.goal.GoalEntity

interface GoalDataSource {

    suspend fun create(goalEntity: GoalEntity)

    suspend fun getGoals(): LiveData<List<GoalEntity>>

    suspend fun update(goalEntity: GoalEntity)

    suspend fun delete(goalEntity: GoalEntity)

}