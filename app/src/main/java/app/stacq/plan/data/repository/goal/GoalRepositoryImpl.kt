package app.stacq.plan.data.repository.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.goal.GoalLocalDataSource
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSource
import app.stacq.plan.domain.Goal
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import app.stacq.plan.domain.asGoal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoalRepositoryImpl(
    private val goalLocalDataSource: GoalLocalDataSource,
    private val goalRemoteDataSource: GoalRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GoalRepository {

    override suspend fun create(goal: Goal) = withContext(ioDispatcher) {
        goalLocalDataSource.create(goal.asEntity())
        goalRemoteDataSource.create(goal.asDocument())
    }

    override suspend fun read(goalId: String): Goal? {
        return goalLocalDataSource.read(goalId)?.asGoal()
    }

    override suspend fun update(goal: Goal) = withContext(ioDispatcher) {
        goalLocalDataSource.update(goal.asEntity())
        goalRemoteDataSource.update(goal.asDocument())
    }

    override suspend fun delete(goal: Goal) = withContext(ioDispatcher) {
        goalLocalDataSource.delete(goal.asEntity())
    }

    override suspend fun updateCategory(goal: Goal, previousCategoryId: String) =
        withContext(ioDispatcher) {
            goalLocalDataSource.update(goal.asEntity())
            goalRemoteDataSource.updateCategory(goal.asDocument(), previousCategoryId)
        }

    override fun getCountGoalCompletedDays(goalId: String): LiveData<Int> {
        return goalLocalDataSource.getCountGoalCompletedDays(goalId)
    }

    override fun getGoals(): LiveData<List<Goal>> =
        goalLocalDataSource.getGoals().map {
            it.map { goalEntity -> goalEntity.asGoal() }
        }

    override fun getGoal(goalId: String): LiveData<Goal> =
        goalLocalDataSource.getGoal(goalId).map {
            it.asGoal()
        }

    override fun getActiveGoals(): LiveData<List<Goal>> =
        goalLocalDataSource.getActiveGoals().map {
            it.map { goalEntity -> goalEntity.asGoal() }
        }
}