package app.stacq.plan.data.source.local.category

import androidx.lifecycle.LiveData
import androidx.room.*
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory

@Dao
interface CategoryDao {

    /**
     * Select all categories from the category.
     *
     * @return all categories.
     */
    @Query(
        "SELECT * FROM category"
    )
    fun getCategories(): LiveData<List<Category>>


    /**
     * Insert a category.
     * If the category already exists.
     *
     * @param category the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: Category)


    /**
     * Delete a category.
     *
     * @param category to be delete
     */
    @Delete
    suspend fun delete(category: Category)


}