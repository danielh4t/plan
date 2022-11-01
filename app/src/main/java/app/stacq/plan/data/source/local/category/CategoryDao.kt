package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Category

@Dao
interface CategoryDao {

    /**
     * Select all categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category WHERE enabled"
    )
    fun getCategories(): LiveData<List<Category>>

    /**
     * Select category id from the category.
     *
     * @return category id.
     */
    @Query(
        "SELECT id FROM category WHERE name=:name"
    )
    fun getCategoryIdByName(name: String): String?

    /**
     * Insert a category.
     * If the category already exists.
     *
     * @param category the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: Category)


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
     * @param category to be delete
     */
    @Delete
    suspend fun delete(category: Category)


}