package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.goal.GoalLocalDataSource
import app.stacq.plan.data.source.model.*
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoalRepository(
    private val localDataSource: GoalLocalDataSource,
    private val remoteDataSource: GoalRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(goal: Goal) = withContext(ioDispatcher) {
        localDataSource.create(goal.asEntity())
        remoteDataSource.create(goal.asDocument())
    }

    suspend fun getGoals(): LiveData<List<Goal>> {
        return localDataSource.getGoals().map {
            it.map { goalEntity -> goalEntity.asGoal() }
        }
    }

    suspend fun update(goal: Goal) = withContext(ioDispatcher) {
        localDataSource.update(goal.asEntity())
        remoteDataSource.update(goal.asDocument())
    }

    suspend fun delete(goal: Goal) = withContext(ioDispatcher) {
        localDataSource.delete(goal.asEntity())
    }

}