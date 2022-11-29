package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Task

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
     * Update the position_at of a task
     *
     * @param id of the task
     * @param positionAt position of task
     */
    @Query("UPDATE task SET position_at = :positionAt WHERE id = :id")
    suspend fun updatePositionAById(id: String, positionAt: Long)

    /**
     * Delete a task by id
     *
     * @param id task to be delete
     */
    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Select all tasks with category name from the task.
     *
     * @return all tasks with category name.
     */
    @Query(
        "SELECT task.id, task.created_at AS createdAt, task.name, task.completed AS completed, " +
                "task.completed_at AS completedAt, task.category_id AS categoryId, " +
                "category.name AS categoryName, category.color AS categoryColor, " +
                "task.timer_finish_at AS timerFinishAt, task.timer_alarm AS timerAlarm, " +
                "task.position_at AS positionAt " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id " +
                "ORDER BY position_at"
    )
    fun getTasksCategory(): LiveData<List<Task>>

    /**
     * Select all tasks from the task.
     *
     * @return all tasks.
     */
    @Query(
        "SELECT task.id, task.created_at AS createdAt, task.name, task.completed AS completed, " +
                "task.completed_at AS completedAt, task.category_id AS categoryId, " +
                "category.name AS categoryName, category.color AS categoryColor, " +
                "task.timer_finish_at AS timerFinishAt, task.timer_alarm AS timerAlarm, " +
                "task.position_at AS positionAt " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id " +
                "WHERE task.id = :id"
    )
    fun getTaskCategoryById(id: String): LiveData<Task>
}