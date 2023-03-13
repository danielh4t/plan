package app.stacq.plan.data.source.local.bite

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteLocalDataSourceImpl(
    private val biteDao: BiteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteLocalDataSource {

    override suspend fun create(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        biteDao.create(biteEntity)
    }

    override suspend fun read(biteId: String): BiteEntity? = withContext(ioDispatcher) {
        return@withContext biteDao.read(biteId)
    }

    override suspend fun update(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        biteDao.update(biteEntity)
    }

    override suspend fun delete(biteEntity: BiteEntity) = withContext(ioDispatcher) {
        biteDao.delete(biteEntity)
    }

    override suspend fun getBitesList(): List<BiteEntity> = withContext(ioDispatcher) {
        biteDao.getBitesList()
    }

    override fun getBites(taskId: String): LiveData<List<BiteEntity>> {
        return biteDao.getBites(taskId)
    }

    override fun getBite(biteId: String): LiveData<BiteEntity> {
        return biteDao.getBite(biteId)
    }
}