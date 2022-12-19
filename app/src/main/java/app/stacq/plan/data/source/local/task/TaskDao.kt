package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    /**
     * Select all tasks from the task.
     *
     * @return all tasks.
     */
    @Query(
        "SELECT * FROM task"
    )
    fun getTasks(): LiveData<List<TaskEntity>>

    @Query(
        "SELECT * FROM task"
    )
    fun getTasksList(): List<TaskEntity>

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
     * @param id
     * @return all tasks.
     */
    @Query("SELECT * FROM task WHERE id = :id")
    fun readById(id: String): LiveData<TaskEntity>


    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param id task id
     * @param name new task name
     * @param categoryId new task category id
     */
    @Query("UPDATE task SET name = :name, category_id = :categoryId WHERE id = :id")
    suspend fun updateNameAndCategoryById(id: String, name: String, categoryId: Int)

    /**
     * Update a task
     *
     * @param taskEntity id of the task
     */
    @Update
    suspend fun update(taskEntity: TaskEntity)

    /**
     * Update the completed and completed_at of a task
     *
     * @param id of the task
     */
    @Query("UPDATE task SET completed = :completed, completed_at = :completedAt WHERE id = :id")
    suspend fun updateCompletionById(id: String, completed: Boolean, completedAt: Long)

    /**
     * Update task timer finish at time
     *
     * @param id of the task
     * @param finishAt timer timer
     */
    @Query("UPDATE task SET timer_finish_at = :finishAt WHERE id = :id")
    suspend fun updateTimerFinishById(id: String, finishAt: Long)

    /**
     * Update the timer alarm of a task
     *
     * @param id of the task
     */
    @Query("UPDATE task SET timer_alarm = NOT timer_alarm WHERE id = :id")
    suspend fun updateTimerAlarmById(id: String)

    /**
     * Update the priority of a task
     *
     * @param id of the task
     * @param priority position of task
     */
    @Query("UPDATE task SET priority = :priority WHERE id = :id")
    suspend fun updatePriority(id: String, priority: Int)

    /**
     * Delete a task
     *
     * @param taskEntity to be delete
     */
    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Transaction
    @Query("SELECT * FROM task ORDER BY priority DESC")
    fun getTasksAndCategory(): LiveData<List<TaskEntityAndCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskAndCategory(id: String): LiveData<TaskEntityAndCategoryEntity>

    @Query("SELECT COUNT(*) as completed, strftime('%j',DATE(completed_at, 'unixepoch')) as day " +
            "FROM task " +
            "WHERE completed and completed_at >= :yearStartAt " +
            "GROUP BY day")
    fun getTaskAnalysis(yearStartAt: Long): LiveData<List<TaskAnalysis>>

}