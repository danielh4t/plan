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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var provider: OAuthProvider.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        provider = OAuthProvider.newBuilder("github.com")
        firebaseAuth = Firebase.auth
        showAuthenticatedUI(firebaseAuth.currentUser)

        binding = ActivityMainBinding.inflate(layoutInflater)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_in -> {
                showAuthenticatedUI(firebaseAuth.currentUser)
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

    private fun handleAuthentication() {
        val pendingResultTask: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener {
                        // User is signed in.
                        Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
                        // authResult.getCredential().getAccessToken().
                    })
                .addOnFailureListener {
                    // Handle failure.
                    Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_SHORT).show()
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            firebaseAuth
                .startActivityForSignInWithProvider( /* activity= */this, provider.build())
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // authResult.getCredential().getAccessToken().
                    Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Handle failure.
                    Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showAuthenticatedUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, user.displayName, Toast.LENGTH_SHORT).show()
        } else {
            handleAuthentication()
        }
    }
}