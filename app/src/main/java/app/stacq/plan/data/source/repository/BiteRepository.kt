package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.model.Bite
import app.stacq.plan.data.source.model.asBite
import app.stacq.plan.data.source.model.asBiteDocument
import app.stacq.plan.data.source.model.asBiteEntity
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteRepository(
    private val localDataSource: BiteLocalDataSource,
    private val remoteDataSource: BiteRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getBites(taskId: String): LiveData<List<Bite>> = withContext(ioDispatcher) {
        localDataSource.getBites(taskId)
            .map { biteEntities: List<BiteEntity> -> biteEntities.map { biteEntity -> biteEntity.asBite() } }
    }

    suspend fun create(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.create(bite.asBiteEntity())
        remoteDataSource.createBite(bite.asBiteDocument())
    }

    suspend fun update(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.update(bite.asBiteEntity())
    }

    suspend fun delete(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.delete(bite.asBiteEntity())
    }

}