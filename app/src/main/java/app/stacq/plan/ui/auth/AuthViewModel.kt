package app.stacq.plan.ui.auth

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<AuthUIState> =
        MutableStateFlow(AuthUIState.Initial)
    val uiState: StateFlow<AuthUIState> =
        _uiState.asStateFlow()

    fun isAuthenticated() {
        _uiState.value = AuthUIState.Loading
        if (Firebase.auth.currentUser == null) { // not authenticated
            _uiState.value = AuthUIState.Unauthenticated
        } else { // authenticated
            _uiState.value = AuthUIState.Authenticated
        }
    }

    fun handleFailure(e: GetCredentialException) {
        Log.e(TAG, "Unexpected type of credential", e)
    }

    suspend fun handleSignIn(result: GetCredentialResponse): String? {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdToken, null)
                        try {

                            val authResult =
                                Firebase.auth.signInWithCredential(firebaseCredential).await()
                            val user = authResult.user
                            if (user != null) {
                                _uiState.value = AuthUIState.Authenticated
                                Log.d(TAG, "signInWithCredential:success" + user.displayName)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "signInWithCredential:failure", e)
                            _uiState.value = AuthUIState.Error("Sign in failure")
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        _uiState.value = AuthUIState.Error("Sign in failure")
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                    _uiState.value = AuthUIState.Error("Sign in failure")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
                _uiState.value = AuthUIState.Error("Sign in failure")
            }
        }
        return null
    }
}

sealed interface AuthUIState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : AuthUIState

    /**
     *  Processing such as checking for existing authenticated
     */
    data object Loading : AuthUIState

    /**
     * User is not authenticated
     */
    data object Unauthenticated : AuthUIState

    /**
     *  Authentication process completed Successfully
     */
    data object Authenticated : AuthUIState

    /**
     * There was an error
     */
    data class Error(val errorMessage: String) : AuthUIState
}
