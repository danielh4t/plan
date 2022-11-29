package app.stacq.plan.data.source.remote.category

import app.stacq.plan.data.source.local.category.CategoryEntity
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

    suspend fun createCategory(categoryEntity: CategoryEntity) = withContext(ioDispatcher) {

        // root collection
        val uid = firebaseAuth.currentUser?.uid

        if(uid != null) {

            val document = categoryEntity.id

            val fields = hashMapOf(
                "name" to categoryEntity.name,
                "color" to categoryEntity.color,
                "count" to 0
            )

            firestore.collection(uid).document(document).set(fields)
        }


    }


}