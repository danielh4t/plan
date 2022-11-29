package app.stacq.plan.data.source.local.bite


import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.BiteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BiteLocalDataSource(
    private val biteDao: BiteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiteDataSource {

    override suspend fun create(biteEntity: BiteEntity) {
        biteDao.create(biteEntity)
    }

    override suspend fun getBites(taskId: String): LiveData<List<BiteEntity>> =
        withContext(ioDispatcher) {
            biteDao.getBites(taskId)
        }

    override suspend fun update(biteEntity: BiteEntity) {
        biteDao.update(biteEntity)
    }

    override suspend fun delete(biteEntity: BiteEntity) {
        biteDao.delete(biteEntity)
    }

    override suspend fun updateCompletionById(id: String, completed: Boolean, completedAt: Long) {
        biteDao.updateCompletionById(id, completed, completedAt)
    }
}