package app.stacq.plan.data.source.remote.category

import app.stacq.plan.util.CalendarUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val PROFILE = "profile"
private const val COMPLETED = "completed"

class CategoryRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        // root collection
        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id
        val year = CalendarUtil().currentYear()

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "id" to categoryDocument.id,
            "createdAt" to categoryDocument.createdAt,
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled
        )

        firestore.collection(uid).document(categoryId).set(fields)

        // Get number of days in year and increment since
        // julian calendar day year 1 - 366 is not zero-based indexed
        val days = CalendarUtil().days()
        val completedField = hashMapOf(
            year to IntArray(days) { 0 }.asList()
        )

        firestore.collection(uid)
            .document(categoryId)
            .collection(PROFILE)
            .document(COMPLETED)
            .set(completedField)
    }


    suspend fun update(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "id" to categoryDocument.id,
            "createdAt" to categoryDocument.createdAt,
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled
        )

        firestore.collection(uid)
            .document(categoryId)
            .set(fields)
    }

    fun getCategories(): Flow<QuerySnapshot> {

        val uid = firebaseAuth.currentUser?.uid ?: return emptyFlow()

        return firestore.collection(uid)
            .whereNotEqualTo("name", null)
            .snapshots()
            .flowOn(ioDispatcher)
    }

    suspend fun getCategoriesDocuments(): List<DocumentSnapshot> = withContext(ioDispatcher) {
        val uid = firebaseAuth.currentUser?.uid ?: return@withContext emptyList()

        return@withContext firestore.collection(uid)
            .whereNotEqualTo("name", null)
            .get()
            .await()
            .documents
    }
}