package app.stacq.plan.data.source.local.goal

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.GoalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoalLocalDataSource(
    private val goalDao: GoalDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GoalDataSource {


    override suspend fun create(goalEntity: GoalEntity) = withContext(ioDispatcher) {
        goalDao.insert(goalEntity)
    }

    override suspend fun getGoals(): LiveData<List<GoalEntity>> = withContext(ioDispatcher) {
        goalDao.getGoals()
    }

    override suspend fun update(goalEntity: GoalEntity) {
        goalDao.update(goalEntity)
    }

    override suspend fun delete(goalEntity: GoalEntity) = withContext(ioDispatcher) {
        goalDao.delete(goalEntity)
    }

}