package app.stacq.plan.data.source.local.bite


import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BiteLocalDataSourceImpl @Inject constructor(
    private val biteDao: BiteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteLocalDataSource {

    override suspend fun create(biteEntity: BiteEntity) {
        biteDao.create(biteEntity)
    }

    override suspend fun update(biteEntity: BiteEntity) {
        biteDao.update(biteEntity)
    }

    override suspend fun delete(biteEntity: BiteEntity) {
        biteDao.delete(biteEntity)
    }

    override suspend fun getBitesList(): List<BiteEntity> = withContext(ioDispatcher) {
        biteDao.getBitesList()
    }

    override fun getBites(taskId: String): LiveData<List<BiteEntity>> {
        return biteDao.getBites(taskId)
    }
}