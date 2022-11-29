package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.BiteDataSource
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.bite.toBiteDocument
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteRepository(
    private val localDataSource: BiteLocalDataSource,
    private val remoteDataSource: BiteRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteDataSource {

    override suspend fun getBites(taskId: String): LiveData<List<BiteEntity>> {
        return localDataSource.getBites(taskId)
    }

    override suspend fun create(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        localDataSource.create(biteEntity)
        remoteDataSource.createBite(biteEntity.toBiteDocument())
    }

    override suspend fun update(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        localDataSource.update(biteEntity)
    }

    override suspend fun delete(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        localDataSource.delete(biteEntity)
    }

    override suspend fun updateCompletionById(id: String, completed: Boolean, completedAt: Long) =
        withContext(ioDispatcher) {
            localDataSource.updateCompletionById(id, completed, completedAt)
        }
}