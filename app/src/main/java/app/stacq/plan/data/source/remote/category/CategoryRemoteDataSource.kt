package app.stacq.plan.data.source.remote.category

import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface CategoryRemoteDataSource {
    suspend fun create(categoryDocument: CategoryDocument)

    suspend fun update(categoryDocument: CategoryDocument)
    fun getCategories(): Flow<QuerySnapshot>
}