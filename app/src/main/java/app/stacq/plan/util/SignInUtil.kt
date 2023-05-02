package app.stacq.plan.util

import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun SignInClient.launchSignIn(clientId: String): Task<BeginSignInResult> {
    val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(clientId)
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .build()

    return beginSignIn(signInRequest)
}

// The user's response to the One Tap sign-in prompt will be reported here
fun SignInClient.handleSignInWithFirebase(data: Intent?) {
    try {
        val credential = getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        when {
            idToken != null -> {
                // Got an ID token from Google.


                // Use it to authenticate with your backend.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                        } else {
                            // If sign in fails, display a message to the user.
                            Firebase.crashlytics.log("Sign In Failure: ${task.exception?.localizedMessage}")
                        }
                    }
            }

            else -> {
                // Shouldn't happen.
                Firebase.crashlytics.log("Sign In Failure: No ID token!")
            }
        }
    } catch (e: ApiException) {
        when (e.statusCode) {
            CommonStatusCodes.CANCELED -> {
                Firebase.crashlytics.log("Sign In Failure: One-tap dialog was closed.")
                // Don't re-prompt the user.
            }

            CommonStatusCodes.NETWORK_ERROR -> {
                // Try again or just ignore.
                Firebase.crashlytics.log("Sign In Failure: One-tap encountered a network error.")
            }

            else -> {
                Firebase.crashlytics.log("Sign In Failure:  ${e.localizedMessage}")
            }

        }
    }
}
