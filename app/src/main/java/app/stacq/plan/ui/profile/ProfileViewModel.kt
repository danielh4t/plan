package app.stacq.plan.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = Firebase.auth
    private val user: FirebaseUser? = firebaseAuth.currentUser

    private val hasUser = MutableLiveData(false)

    init {
        if (user != null) hasUser.value = true
    }

}