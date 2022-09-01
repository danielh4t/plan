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
        "SELECT task.id, task.title, task.completed_at AS completedAt, " +
                "task.completed AS completed, category.name AS categoryName " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id"
    )
    fun getTaskCategoryAll(): LiveData<List<TaskCategory>>

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
    @Query(
        "SELECT task.id, task.title, task.completed AS completed, " +
                "task.completed_at AS completedAt,  category.name AS categoryName " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id " +
                "WHERE task.id = :id"
    )
    fun getTaskCategoryById(id: String): LiveData<TaskCategory>

    /**
     * Select all tasks from the task.
     *
     * @param id
     * @return all tasks.
     */
    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: String): LiveData<Task>


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
     * @param id task id
     * @param title new task title
     * @param categoryId new task category id
     */
    @Query("UPDATE task SET title = :title, category_id = :categoryId WHERE id = :id")
    suspend fun updateTaskTitleAndCategoryById(id: String, title: String, categoryId: Int)


    /**
     * Delete a task. by id
     *
     * @param id task to be delete
     */
    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Update the complete status of a task
     *
     * @param id id of the task
     * @param completed status of task to be updated
     */
    @Query("UPDATE task SET completed = :completed, completed_at = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletedById(id: String, completed: Boolean, completedAt: Long)

}