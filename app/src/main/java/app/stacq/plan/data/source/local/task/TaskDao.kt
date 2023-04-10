package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*

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

    /**
     * Update the archive of a task to true
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET archived = true WHERE id = :taskId")
    suspend fun archive(taskId: String)

    /**
     * Update the archive of a task to false
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET archived = false WHERE id = :taskId")
    suspend fun unarchive(taskId: String)

    /**
     * Update the completed and completed_at of a task
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET completed = :completed, completed_at = :completedAt WHERE id = :taskId")
    suspend fun updateCompletionById(taskId: String, completed: Boolean, completedAt: Long)

    /**
     * Update task timer finish at time
     *
     * @param taskId of the task
     * @param finishAt timer timer
     */
    @Query("UPDATE task SET timer_finish_at = :finishAt WHERE id = :taskId")
    suspend fun updateTimerFinishById(taskId: String, finishAt: Long)

    /**
     * Update the timer alarm of a task
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET timer_alarm = NOT timer_alarm WHERE id = :taskId")
    suspend fun updateTimerAlarmById(taskId: String)

    /**
     * Update the priority of a task
     *
     * @param taskId of the task
     * @param priority position of task
     */
    @Query("UPDATE task SET priority = :priority WHERE id = :taskId")
    suspend fun updatePriority(taskId: String, priority: Int)

    @Query("SELECT * FROM task")
    fun getTasksList(): List<TaskEntity>


    @Query("SELECT COUNT(*) > 0 FROM task WHERE goal_id = :goalId AND NOT completed")
    fun hasGeneratedTask(goalId: String): Boolean

    @Transaction
    @Query("SELECT * FROM task WHERE NOT archived ORDER BY priority DESC")
    fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskAndCategory(taskId: String): LiveData<TaskEntityAndCategoryEntity>
}