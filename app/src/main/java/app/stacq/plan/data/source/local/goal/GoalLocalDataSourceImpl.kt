package app.stacq.plan.data.source.local.goal


import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoalLocalDataSourceImpl(
    private val goalDao: GoalDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GoalLocalDataSource {

    override suspend fun create(goalEntity: GoalEntity) = withContext(ioDispatcher) {
        goalDao.create(goalEntity)
    }

    override suspend fun read(goalId: String): GoalEntity?  = withContext(ioDispatcher) {
        return@withContext goalDao.read(goalId)
    }

    override suspend fun update(goalEntity: GoalEntity) = withContext(ioDispatcher) {
        goalDao.update(goalEntity)
    }

    override suspend fun delete(goalEntity: GoalEntity) = withContext(ioDispatcher) {
        goalDao.delete(goalEntity)
    }

    override fun getCountGoalCompletedDays(goalId: String): LiveData<Int> {
        return goalDao.getCountGoalCompletedDays(goalId)
    }

    override fun getGoals(): LiveData<List<GoalEntityAndCategoryEntity>> {
        return goalDao.getGoals()
    }

    override fun getGoal(goalId: String): LiveData<GoalEntityAndCategoryEntity> {
        return goalDao.getGoal(goalId)
    }

    override fun getActiveGoals(): LiveData<List<GoalEntityAndCategoryEntity>> {
        return goalDao.getActiveGoals()
    }
}