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

    @Upsert
    fun upsert(taskEntity: TaskEntity)

    /**
     * Update the archive of a task to true
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET archived = 1 WHERE id = :taskId")
    suspend fun archive(taskId: String)

    /**
     * Update the archive of a task to false
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET archived = 0 WHERE id = :taskId")
    suspend fun unarchive(taskId: String)

    /**
     * Update the completed and completed_at of a task
     *
     * @param taskId of the task
     */
    @Query("UPDATE task SET started_at = :startedAt, completed_at = :completedAt WHERE id = :taskId")
    suspend fun updateStartCompletionById(taskId: String, startedAt: Long, completedAt: Long)

    /**
     * Update task timer finish at time
     *
     * @param taskId of the task
     * @param finishAt timer timer
     */
    @Query("UPDATE task SET timer_finish_at = :finishAt, " +
            "started_at = strftime('%s', 'now') WHERE id = :taskId")
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

    @Query(
        "SELECT COUNT(*) > 0 FROM task " +
                "WHERE goal_id = :goalId " +
                "AND NOT archived"
    )
    fun hasGeneratedTask(goalId: String): Boolean

    @Query(
        "SELECT COUNT(*) > 0 " +
                "FROM task " +
                "WHERE goal_id = :goalId " +
                "AND strftime('%Y-%m-%d', datetime(completed_at, 'unixepoch')) = strftime('%Y-%m-%d', 'now') " +
                "AND (completed_at != 0 OR archived)"
    )
    fun hasCompletedTaskGoalToday(goalId: String): Boolean

    @Query("SELECT COUNT(*) FROM task WHERE completed_at != 0 AND " +
            "strftime('%Y-%m-%d', datetime(completed_at, 'unixepoch', 'localtime')) = date('now', 'localtime')")
    fun getCompletedToday(): LiveData<Int>

    @Query("SELECT COUNT(distinct goal_id) FROM task WHERE completed_at != 0 AND goal_id NOT NULL AND " +
            "strftime('%Y-%m-%d', datetime(completed_at, 'unixepoch', 'localtime')) = date('now', 'localtime')")
    fun getCompletedTaskGoalToday(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM task WHERE NOT archived")
    fun getCount(): LiveData<Int>

    @Transaction
    @Query("SELECT * FROM task WHERE NOT archived ORDER BY priority DESC")
    fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskAndCategory(taskId: String): LiveData<TaskEntityAndCategoryEntity>

    @Transaction
    @Query("SELECT * FROM task WHERE completed_at != 0 ORDER BY completed_at")
    fun getCompletedTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>
}