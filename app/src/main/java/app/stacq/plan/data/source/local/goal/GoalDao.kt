package app.stacq.plan.data.source.local.goal

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GoalDao {

    /**
     * Insert a goal
     * If the goal already exists, ignore it.
     *
     * @param goalEntity the goa to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(goalEntity: GoalEntity)

    /**
     * Select goal.
     *
     * @param goalId
     * @return a goal or null.
     */
    @Query("SELECT * FROM goal WHERE id = :goalId")
    suspend fun read(goalId: String): GoalEntity?

    /**
     * Update a goal
     *
     * @param goalEntity
     */
    @Update
    suspend fun update(goalEntity: GoalEntity)

    /**
     * Soft delete a goal
     *
     * @param goalId id to delete
     */
    @Query("UPDATE goal SET archived = 1 WHERE id = :goalId")
    suspend fun delete(goalId: String)

    @Upsert
    suspend fun upsert(goalEntity: GoalEntity)

    @Query(
        "WITH cte AS ( " +
                "SELECT count(t.completed_at != 0) as completed_days " +
                "FROM goal AS g " +
                "JOIN task AS t ON g.id = t.goal_id " +
                "WHERE g.id = :goalId " +
                "AND t.completed_at IS NOT 0 " +
                "GROUP BY strftime('%j',DATE(t.completed_at, 'unixepoch'))) " +
                "SELECT count(completed_days) " +
                "FROM cte"
    )
    suspend fun getCountGoalCompletedDays(goalId: String): Int

    /**
     * Select all goal entities from the goal table as a list.
     *
     * @return all goal.
     */
    @Query("SELECT * FROM goal")
    fun getGoalEntities(): List<GoalEntity>

    @Query("SELECT COUNT(*) FROM goal WHERE completed_at != 0")
    fun getCount(): LiveData<Int>

    /**
     * Select all goals.
     *
     * @return goals.
     */
    @Transaction
    @Query("SELECT * FROM goal WHERE NOT archived ORDER BY name")
    fun getGoals(): LiveData<List<GoalEntityAndCategoryEntity>>

    /**
     * Select goal.
     * @param goalId
     * @return goals.
     */
    @Transaction
    @Query("SELECT * FROM goal WHERE id = :goalId")
    fun getGoal(goalId: String): LiveData<GoalEntityAndCategoryEntity>

    /**
     * Select all active(completed = false) goals.
     *
     * @return goals.
     */
    @Transaction
    @Query("SELECT * FROM goal WHERE completed_at != 0 ORDER BY category_id DESC")
    fun getActiveGoals(): LiveData<List<GoalEntityAndCategoryEntity>>

    /**
     * Select all active(completed = false) goals that generate is true.
     *
     * @return goals.
     */
    @Transaction
    @Query("SELECT * FROM goal WHERE generate AND completed_at = 0")
    fun getGenerateGoals(): List<GoalEntity>
}
