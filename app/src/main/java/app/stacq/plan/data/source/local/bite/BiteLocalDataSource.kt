package app.stacq.plan.data.source.local.bite

import androidx.lifecycle.LiveData

interface BiteLocalDataSource {

    suspend fun create(biteEntity: BiteEntity)

    suspend fun read(biteId: String): BiteEntity?

    suspend fun update(biteEntity: BiteEntity)

    suspend fun delete(biteEntity: BiteEntity)

    suspend fun getBitesList(): List<BiteEntity>

    fun getBites(taskId: String): LiveData<List<BiteEntity>>
    fun getBite(biteId: String):  LiveData<BiteEntity>
}