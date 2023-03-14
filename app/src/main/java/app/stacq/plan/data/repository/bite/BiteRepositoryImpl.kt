package app.stacq.plan.data.repository.bite

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


class BiteRepositoryImpl(
    private val biteLocalDataSource: BiteLocalDataSource,
    private val biteRemoteDataSource: BiteRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteRepository {
    override suspend fun create(bite: Bite) = withContext(ioDispatcher) {
        biteLocalDataSource.create(bite.asEntity())
        biteRemoteDataSource.create(bite.asDocument())
    }

    override suspend fun read(biteId: String): Bite? {
        return biteLocalDataSource.read(biteId)?.asBite()
    }

    override suspend fun update(bite: Bite) = withContext(ioDispatcher) {
        biteLocalDataSource.update(bite.asEntity())
        biteRemoteDataSource.update(bite.asDocument())
    }

    override suspend fun delete(bite: Bite) = withContext(ioDispatcher) {
        biteLocalDataSource.delete(bite.asEntity())
    }

    override fun getBites(taskId: String): LiveData<List<Bite>> =
        Transformations.map(biteLocalDataSource.getBites(taskId)) {
            it?.map { biteEntity: BiteEntity -> biteEntity.asBite() }
        }

    override fun getBite(biteId: String): LiveData<Bite> =
        Transformations.map(biteLocalDataSource.getBite(biteId)) {
            it?.asBite()
    }
}