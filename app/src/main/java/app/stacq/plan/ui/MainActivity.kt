package app.stacq.plan.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.ActivityMainBinding
import app.stacq.plan.util.installCheckProviderFactory
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        installCheckProviderFactory(firebaseAppCheck)


        binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = navController()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.sign_in)?.let { showAuthenticatedUI(firebaseAuth.currentUser, it) }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_in -> {
                signInUser(firebaseAuth.currentUser)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() =
        navController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private fun navController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            invalidateOptionsMenu()
            Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
        } else {
            // Sign in failed
            // If response is null the user canceled the sign-in flow using the back button.
            if (response == null) {
                Toast.makeText(this, R.string.sign_in_dismiss, Toast.LENGTH_SHORT).show()
                throw RuntimeException("Test Crash")
            } else {
                response.error?.let {
                    val params = Bundle()
                    params.putInt("login_error", it.errorCode)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
                }
                Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showAuthenticatedUI(user: FirebaseUser?, menuItem: MenuItem) {
        if (user != null) menuItem.setIcon(R.drawable.ic_person_done_outline)
    }

    private fun signInUser(user: FirebaseUser?) {
        if (user != null) {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    invalidateOptionsMenu()
                    Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, R.string.sign_out_failure, Toast.LENGTH_SHORT).show()
                }

        } else {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
            )
            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.Theme_Plan)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }

}