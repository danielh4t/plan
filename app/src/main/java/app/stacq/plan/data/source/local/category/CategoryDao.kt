package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {

    /**
     * Select enabled categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category  WHERE enabled"
    )
    fun getEnabledCategories(): LiveData<List<CategoryEntity>>


    /**
     * Select all categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category"
    )
    fun getCategories(): LiveData<List<CategoryEntity>>

    /**
     * Insert a category.
     * If the category already exists.
     *
     * @param categoryEntity the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(categoryEntity: CategoryEntity)


    /**
     * Update the category enabled
     *
     * @param id of the category
     */
    @Query("UPDATE category SET enabled = NOT enabled WHERE id = :id")
    suspend fun updateEnabledById(id: String)

    /**
     * Delete a category.
     *
     * @param categoryEntity to be delete
     */
    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    /**
     * Count categories.
     *
     * @return number of enabled categories.
     */
    @Query("SELECT COUNT(*) FROM category WHERE enabled")
    fun getCategoriesCount(): Int


    /**
     * Select all categories from the category.
     *
     * @return all categories.
     */
    @Query("SELECT * FROM category")
    fun getCategoriesList(): List<CategoryEntity>

}