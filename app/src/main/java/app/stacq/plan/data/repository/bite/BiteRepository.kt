package app.stacq.plan.data.repository.bite

import androidx.lifecycle.LiveData
import app.stacq.plan.domain.Bite

interface BiteRepository {
    suspend fun create(bite: Bite)

    suspend fun read(biteId: String): Bite?

    suspend fun update(bite: Bite)

    suspend fun delete(bite: Bite)

    fun getBites(taskId: String): LiveData<List<Bite>>
    fun getBite(biteId: String): LiveData<Bite>
}