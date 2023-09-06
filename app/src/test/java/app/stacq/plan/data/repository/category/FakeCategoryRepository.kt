package app.stacq.plan.data.repository.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.stacq.plan.domain.Category

class FakeCategoryRepository: CategoryRepository {

    private val categories = MutableLiveData<List<Category>>()

    override fun getEnabledCategories(): LiveData<List<Category>> {
        return categories
    }

    override suspend fun create(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun update(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun updateEnabled(categoryId: String) {
        TODO("Not yet implemented")
    }

    override fun getCount(): LiveData<Int> {
        TODO("Not yet implemented")
    }

    override fun getCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getAllCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getCategory(categoryId: String): LiveData<Category> {
        TODO("Not yet implemented")
    }
}