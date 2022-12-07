package app.stacq.plan.ui

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.ActivityMainBinding
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.installCheckProviderFactory
import app.stacq.plan.util.launchSignIn
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var oneTapClient: SignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installCheckProviderFactory()

        firebaseAuth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks, R.id.nav_categories, R.id.nav_profile
            )
        )

        val navController = navController()
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // reference bottom navigation view
        val navView: BottomNavigationView = binding.bottomNavigation
        // navigation controller to bottom navigation view
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_UNLABELED

        // if user isn't signed in and hasn't already declined to use One Tap sign-in
        if (showOneTapUI && firebaseAuth.currentUser == null) {
            val clientId = getString(R.string.default_web_client_id)
            oneTapClient.launchSignIn(clientId)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("MainActivity", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("MainActivity", "Failure: ${e.localizedMessage}")
                }
            // don't
            showOneTapUI = false
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

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                oneTapClient.handleSignInWithFirebase(it.data, firebaseAuth)
            }
        }
}