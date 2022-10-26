package app.stacq.plan.data.source.remote.category

import app.stacq.plan.data.model.Category
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

    suspend fun createCategory(category: Category) = withContext(ioDispatcher) {

        // root collection
        val uid = firebaseAuth.currentUser?.uid

        if(uid != null) {

            val document = category.id.toString()

            val fields = hashMapOf(
                "name" to category.name,
                "color" to category.color,
                "count" to 0
            )

            firestore.collection(uid).document(document).set(fields)
        }


    }


}