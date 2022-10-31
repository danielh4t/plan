package app.stacq.plan.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = Firebase.auth

    val user = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)

    fun updateUser() {
        user.value = firebaseAuth.currentUser
    }

}