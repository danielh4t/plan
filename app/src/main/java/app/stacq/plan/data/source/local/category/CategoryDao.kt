package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {



    /**
     * Insert a category.
     * If the category already exists.
     *
     * @param categoryEntity the category to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: CategoryEntity)

    /**
     * Update a category.
     * If the category already exists.
     *
     * @param categoryEntity the category to be inserted.
     */
    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(categoryEntity: CategoryEntity)

    @Upsert
    fun upsert(categoryEntity: CategoryEntity)

    /**
     * Update the category enabled
     *
     * @param categoryId of the category
     */
    @Query("UPDATE category SET enabled = NOT enabled WHERE id = :categoryId")
    suspend fun updateEnabledById(categoryId: String)

    /**
     * Soft delete a category and disable.
     *
     * @param categoryId to be delete
     */
    @Query("UPDATE category SET deleted = 1, enabled = 0 WHERE id = :categoryId")
    suspend fun delete(categoryId: String)

    /**
     * Count categories.
     *
     * @return number of enabled categories.
     */
    @Query("SELECT COUNT(*) FROM category WHERE enabled")
    fun getCategoriesCount(): Int


    /**
     * Select all category entities from the category table as a list.
     *
     * @return all categories.
     */
    @Query("SELECT * FROM category")
    fun getCategoryEntities(): List<CategoryEntity>


    /**
     * Select enabled categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category WHERE enabled ORDER By name"
    )
    fun getEnabledCategories(): LiveData<List<CategoryEntity>>

    /**
     * Select categories from the category that are not deleted.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category WHERE NOT deleted ORDER By name"
    )
    fun getCategories(): LiveData<List<CategoryEntity>>


    /**
     * Select all categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category"
    )
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query(
        "SELECT * FROM category WHERE id = :categoryId"
    )
    fun getCategory(categoryId: String): LiveData<CategoryEntity>

}