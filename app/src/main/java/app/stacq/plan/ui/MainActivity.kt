package app.stacq.plan.ui

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.ActivityMainBinding
import app.stacq.plan.util.REQ_ONE_TAP
import app.stacq.plan.util.beginSignIn
import app.stacq.plan.util.installCheckProviderFactory
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var oneTapClient: SignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var showOneTapUI = true

    private val logTag = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installCheckProviderFactory()

        firebaseAuth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks, R.id.nav_categories, R.id.nav_profile
            )
        )

        val navController = navController()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // reference bottom navigation view
        val navView: BottomNavigationView = binding.bottomNavigation
        // navigation controller to bottom navigation view
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_UNLABELED

        // if user isn't signed in and hasn't already declined to use One Tap sign-in
        if (showOneTapUI && firebaseAuth.currentUser == null) {
            beginSignIn(this, oneTapClient)
                .addOnSuccessListener { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(logTag, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(logTag, "Failure: ${e.localizedMessage}")
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser !== null) showOneTapUI = false
    }

    override fun onSupportNavigateUp() = navController().navigateUp() || super.onSupportNavigateUp()

    private fun navController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    // The user's response to the One Tap sign-in prompt will be reported here
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google.
                            Log.d(logTag, "Got ID token.")
                            // Use it to authenticate with your backend.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(logTag, "signInWithCredential:success")
                                        val user = firebaseAuth.currentUser
                                        showOneTapUI = false
                                        //updateUI(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(
                                            logTag,
                                            "signInWithCredential:failure",
                                            task.exception
                                        )
                                        //updateUI(null)
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(logTag, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(logTag, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            // Try again or just ignore.
                            Log.d(logTag, "One-tap encountered a network error.")
                        }
                        else -> {
                            Log.d(logTag, "Couldn't get credential ${e.localizedMessage}")
                        }
                    }
                }
            }
        }
    }
}