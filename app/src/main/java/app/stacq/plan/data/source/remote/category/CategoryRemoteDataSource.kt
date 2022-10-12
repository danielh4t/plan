package app.stacq.plan.data.source.remote.category

import app.stacq.plan.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun createCategory(category: Category) = withContext(ioDispatcher) {
        val categoryMap = hashMapOf(
            "name" to category.name,
            "color" to category.color
        )
        firestore.collection("categories").document(category.id.toString()).set(categoryMap)
    }


}