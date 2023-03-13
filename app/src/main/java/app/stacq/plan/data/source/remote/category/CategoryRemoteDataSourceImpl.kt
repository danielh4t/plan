package app.stacq.plan.data.source.remote.category

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class CategoryRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRemoteDataSource {

    override suspend fun create(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        // root collection
        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "id" to categoryDocument.id,
            "createdAt" to categoryDocument.createdAt,
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled,
            "deleted" to categoryDocument.deleted,
        )

        firestore.collection(uid).document(categoryId).set(fields)
    }

    override suspend fun update(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "id" to categoryDocument.id,
            "createdAt" to categoryDocument.createdAt,
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled,
            "deleted" to categoryDocument.deleted,
        )

        firestore.collection(uid)
            .document(categoryId)
            .set(fields)
    }

    override suspend fun delete(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id

        if (uid == null || categoryId == null) return@withContext

        val fields = mapOf(
            "deleted" to categoryDocument.deleted
        )

        firestore.collection(uid)
            .document(categoryId)
            .update(fields)
    }

    override suspend fun getCategories(): List<CategoryDocument> {

        val uid = firebaseAuth.currentUser?.uid ?: return emptyList()

        return firestore.collection(uid)
            .get()
            .await()
            .toObjects(CategoryDocument::class.java)
            .toList()
    }
}