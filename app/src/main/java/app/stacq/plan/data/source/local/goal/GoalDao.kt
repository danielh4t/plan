package app.stacq.plan.data.source.local.goal

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity

@Dao
interface GoalDao {

    /**
     * Select all goals
     *
     * @return all goals
     */
    @Query(
        "SELECT * FROM goal"
    )
    fun getGoals(): LiveData<List<GoalEntity>>

    /**
     * Select all goals with category
     *
     * @return all goals with category
     */
    @Transaction
    @Query("SELECT * FROM goal")
    fun getGoalsAndCategory(): LiveData<List<GoalEntityAndCategoryEntity>>

    /**
     * Insert a goal
     * If the goal already exists abort
     *
     * @param goalEntity to insert
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(goalEntity: GoalEntity)

    /**
     * Update a goal
     *
     * @param goalEntity to update
     */
    @Update
    suspend fun update(goalEntity: GoalEntity)

    /**
     * Delete a goal
     *
     * @param goalEntity to be delete
     */
    @Delete
    suspend fun delete(goalEntity: GoalEntity)

}