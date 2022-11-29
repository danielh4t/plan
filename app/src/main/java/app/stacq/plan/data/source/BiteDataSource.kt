package app.stacq.plan.data.source

import androidx.lifecycle.LiveData
import app.stacq.plan.data.source.local.bite.BiteEntity

interface BiteDataSource {

    suspend fun create(biteEntity: BiteEntity)

    suspend fun getBites(taskId: String): LiveData<List<BiteEntity>>

    suspend fun update(biteEntity: BiteEntity)

    suspend fun delete(biteEntity: BiteEntity)

    suspend fun updateCompletionById(id: String, completed: Boolean, completedAt: Long)

}