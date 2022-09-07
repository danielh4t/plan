package app.stacq.plan.data.source.local.task

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

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
    fun getTasks(): LiveData<List<Task>>


    /**
     * Insert a task.
     * If the task already exists, ignore it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createTask(task: Task)

    /**
     * Select all tasks from the task.
     *
     * @param id
     * @return all tasks.
     */
    @Query("SELECT * FROM task WHERE id = :id")
    fun readTaskById(id: String): LiveData<Task>


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
     * Update a task
     *
     * @param task id of the task
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Update the completed and completed_at of a task
     *
     * @param id of the task
     */
    @Query("UPDATE task SET completed = NOT completed, completed_at = strftime('%s','now') WHERE id = :id")
    suspend fun updateTaskCompletionById(id: String)

    /**
     * Update task timer finish at time
     *
     * @param id of the task
     * @param finishAt timer timer
     */
    @Query("UPDATE task SET timer_finish_at = :finishAt WHERE id = :id")
    suspend fun updateTaskTimerById(id: String, finishAt: Long)


    /**
     * Delete a task. by id
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
        "SELECT task.id, task.created_at AS createdAt,task.title, task.completed_at AS completedAt, " +
                "task.completed AS completed, category.name AS categoryName, " +
                "task.timer_finish_at AS timerFinishAt " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id"
    )
    fun getTasksCategory(): LiveData<List<TaskCategory>>

    /**
     * Select all tasks from the task.
     *
     * @return all tasks.
     */
    @Query(
        "SELECT task.id, task.created_at AS createdAt, task.title, task.completed AS completed, " +
                "task.completed_at AS completedAt,  category.name AS categoryName, " +
                "task.timer_finish_at AS timerFinishAt " +
                "FROM task " +
                "JOIN category ON category.id = task.category_id " +
                "WHERE task.id = :id"
    )
    fun readTaskCategoryById(id: String): LiveData<TaskCategory>


}