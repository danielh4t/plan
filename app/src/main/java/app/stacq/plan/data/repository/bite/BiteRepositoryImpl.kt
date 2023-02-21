package app.stacq.plan.data.repository.bite

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.domain.Bite
import app.stacq.plan.domain.asBite
import app.stacq.plan.domain.asDocument
import app.stacq.plan.domain.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteRepositoryImpl(
    private val localDataSource: BiteLocalDataSourceImpl,
    private val remoteDataSource: BiteRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteRepository {
    override suspend fun create(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.create(bite.asEntity())
        remoteDataSource.create(bite.asDocument())
    }

    override suspend fun update(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.update(bite.asEntity())
        remoteDataSource.update(bite.asDocument())
    }

    override suspend fun delete(bite: Bite) = withContext(ioDispatcher) {
        localDataSource.delete(bite.asEntity())
    }

    override fun getBites(taskId: String): LiveData<List<Bite>> =
        Transformations.map(localDataSource.getBites(taskId)) {
            it?.map { biteEntity: BiteEntity -> biteEntity.asBite() }
        }
}