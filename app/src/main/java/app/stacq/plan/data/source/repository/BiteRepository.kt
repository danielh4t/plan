package app.stacq.plan.data.source.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.asBite
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteRepository(
    private val localDataSource: BiteLocalDataSource,
    private val remoteDataSource: BiteRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.create(bite.asEntity())
        remoteDataSource.create(bite.asDocument())
    }

    suspend fun update(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.update(bite.asEntity())
        remoteDataSource.update(bite.asDocument())
    }

    suspend fun delete(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.delete(bite.asEntity())
    }

    suspend fun getBitesList(): List<BiteEntity> = withContext(ioDispatcher) {
        localDataSource.getBitesList()
    }

    suspend fun sync(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        remoteDataSource.update(biteEntity.asDocument())
    }

    fun getBites(taskId: String): LiveData<List<Bite>> =
        Transformations.map(localDataSource.getBites(taskId)) {
            it?.map { biteEntity: BiteEntity -> biteEntity.asBite() }
        }
}