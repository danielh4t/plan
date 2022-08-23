package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Task

@Dao
interface TaskDao {

    /**
     * Select all tasks from the task_table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM task_table")
    fun getTasks(): LiveData<List<Task>>

    /**
     * Insert a task in the database.
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
    @Query("UPDATE task_table SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskIsCompletedById(taskId: String, isCompleted: Boolean)

}