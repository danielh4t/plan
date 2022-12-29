package app.stacq.plan.data.source.remote.category

import app.stacq.plan.util.currentYear
import app.stacq.plan.util.days
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
        val year = currentYear()

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled
        )

        firestore.collection(uid).document(categoryId).set(fields)

        // Get number of days in year and increment since
        // julian calendar day year 1 - 366 is not zero-based indexed
        val days = days() + 1
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
            "name" to categoryDocument.name,
            "color" to categoryDocument.color,
            "enabled" to categoryDocument.enabled
        )

        firestore.collection(uid)
            .document(categoryId)
            .set(fields)
    }


}