package app.stacq.plan.ui

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import app.stacq.plan.R
import app.stacq.plan.databinding.ActivityMainBinding
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.installCheckProviderFactory
import app.stacq.plan.util.launchSignIn
import app.stacq.plan.worker.SyncBiteWorker
import app.stacq.plan.worker.SyncCategoryWorker
import app.stacq.plan.worker.SyncTaskWorker
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
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

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var oneTapClient: SignInClient
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks, R.id.nav_categories, R.id.nav_profile
            )
        )

        val navController = navController()
        setSupportActionBar(binding.topAppBar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNavView: BottomNavigationView = binding.bottomNavigation
        bottomNavView.setupWithNavController(navController)
        bottomNavView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_UNLABELED

        navController.addOnDestinationChangedListener { _, _, arguments ->
            bottomNavView.isVisible = arguments?.getBoolean("ShowBottomNav", false) == true
        }

        binding.accountImageView.setOnClickListener {
            handleAuthentication()
        }

        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                if (user.photoUrl !== null) {
                    binding.accountImageView.load(user.photoUrl) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.accountImageView))
                        transformations(CircleCropTransformation())
                    }
                }
            } else {
                // signed out
                binding.accountImageView.setImageResource(R.drawable.ic_account_circle)
            }
        }
        oneTapClient = Identity.getSignInClient(this)
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installCheckProviderFactory()
        Firebase.auth.addAuthStateListener(authStateListener)

        reportFullyDrawn()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = Firebase.auth.currentUser
        if (user !== null) {
            showOneTapUI = false
            handleSync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.auth.removeAuthStateListener(authStateListener)
    }

    override fun onSupportNavigateUp() = navController().navigateUp() || super.onSupportNavigateUp()

    private fun navController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun handleAuthentication() {
        // if user isn't signed in and hasn't already declined to use One Tap sign-in
        if (showOneTapUI && Firebase.auth.currentUser == null) {
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
        } else {
            // sign out
            Firebase.auth.signOut()
            oneTapClient.signOut()
            showOneTapUI = true
        }
    }

    private fun handleSync() {

        val workManager = WorkManager.getInstance(application)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncCategory = OneTimeWorkRequestBuilder<SyncCategoryWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.CATEGORIES)
            .build()

        val syncTask = OneTimeWorkRequestBuilder<SyncTaskWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.TASKS)
            .build()

        val syncBite = OneTimeWorkRequestBuilder<SyncBiteWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAGS.BITES)
            .build()

        workManager.beginUniqueWork(
            WorkerConstants.UNIQUE.TASKS,
            ExistingWorkPolicy.KEEP,
            syncCategory
        )
            .then(syncTask)
            .then(syncBite)
            .enqueue()
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                oneTapClient.handleSignInWithFirebase(it.data)
            }
        }
}