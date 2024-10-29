package app.stacq.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isUserAuthenticated = MutableStateFlow(false)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated.asStateFlow()

    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    init {
        setupAuthStateListener()
    }

    private fun setupAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _isUserAuthenticated.value = user != null
        }
        authStateListener?.let { Firebase.auth.addAuthStateListener(it) }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                Firebase.auth.signOut()
                // AuthStateListener will handle the state update
            } catch (e: Exception) {
                // _authState.value = AuthUIState.Error("Sign out failed: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { Firebase.auth.removeAuthStateListener(it) }
    }
}
