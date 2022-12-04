package app.stacq.plan.data.source.remote.category

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryRemoteDataSource(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun create(categoryDocument: CategoryDocument) = withContext(ioDispatcher) {

        // root collection
        val uid = firebaseAuth.currentUser?.uid
        val categoryId = categoryDocument.id

        if (uid == null || categoryId == null) return@withContext

        val fields = hashMapOf(
            "name" to categoryDocument.name,
            "color" to categoryDocument.color
        )

        firestore.collection(uid).document(categoryId).set(fields)

    }


}