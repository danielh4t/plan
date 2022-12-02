package app.stacq.plan.data.source.local.bite

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BiteDao {

    /**
     * Select all bite of a task.
     *
     * @return task bits.
     */
    @Query(
        "SELECT * FROM bite WHERE task_id =:taskId"
    )
    fun getBites(taskId: String): LiveData<List<BiteEntity>>

    /**
     * Insert a bite
     * If the bite already exists, ignore it.
     *
     * @param biteEntity the bite to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(biteEntity: BiteEntity)

    /**
     * Update a bite
     *
     * @param biteEntity id of the bite
     */
    @Update
    suspend fun update(biteEntity: BiteEntity)

    /**
     * Delete a bite by id
     *
     * @param biteEntity to be delete
     */
    @Delete
    suspend fun delete(biteEntity: BiteEntity)

}
