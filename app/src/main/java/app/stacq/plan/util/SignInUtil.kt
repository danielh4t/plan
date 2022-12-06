package app.stacq.plan.util

import android.app.Activity
import app.stacq.plan.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task

const val REQ_ONE_TAP = 7

fun beginSignIn(activity: Activity, oneTapClient: SignInClient): Task<BeginSignInResult> {
    val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .build()

    return oneTapClient.beginSignIn(signInRequest)
}