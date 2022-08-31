package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

@Dao
interface TaskDao {

    /**
     * Select all tasks with category name from the task.
     *
     * @return all tasks.
     */
    @Query(
        "SELECT task.id, task.title,task.is_completed AS isCompleted, " +
                "category.name AS categoryName " +
                "FROM task, category " +
                "WHERE category.id = task.category_id"
    )
    fun getTaskAndCategoryNames(): LiveData<List<TaskCategory>>

    /**
     * Select all tasks from the task.
     *
     * @return all tasks.
     */
    @Query(
        "SELECT * FROM task"
    )
    fun getTasks(): LiveData<List<Task>>


    /**
     * Select all tasks from the task.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskById(taskId: String): LiveData<Task>


    /**
     * Insert a task.
     * If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param task new value to write
     */
    @Update
    suspend fun update(task: Task)

    /**
     * Delete a task.
     *
     * @param task task to be delete
     */
    @Delete
    suspend fun delete(task: Task)

    /**
     * Update the complete status of a task
     *
     * @param taskId id of the task
     * @param isCompleted status of task to be updated
     */
    @Query("UPDATE task SET is_completed = :isCompleted, completed_at = :completedAt WHERE id = :taskId")
    suspend fun updateTaskIsCompletedById(taskId: String, isCompleted: Boolean, completedAt: Long)

}