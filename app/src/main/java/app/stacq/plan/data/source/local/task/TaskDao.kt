package app.stacq.plan.data.source.local.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    /**
     * Insert a task.
     * If the task already exists, ignore it.
     *
     * @param taskEntity the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(taskEntity: TaskEntity)

    /**
     * Select all tasks from the task.
     *
     * @param taskId
     * @return all tasks.
     */
    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun read(taskId: String): TaskEntity?

    /**
     * Update a task
     *
     * @param taskEntity id of the task
     */
    @Update
    suspend fun update(taskEntity: TaskEntity)

    /**
     * Delete a task
     *
     * @param taskEntity to be delete
     */
    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Upsert
    fun upsert(taskEntity: TaskEntity)

    @Transaction
    @Query("SELECT * FROM task WHERE completed_at IS NULL ORDER BY created_at ASC LIMIT 1")
    fun getTask(): Flow<TaskEntity?>

    @Transaction
    @Query("SELECT * FROM task ORDER BY created_at DESC")
    fun getTasks(): Flow<List<TaskEntity>>
}