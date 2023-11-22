package app.stacq.plan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.ActivityMainBinding
import app.stacq.plan.util.installCheckProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import io.sentry.Sentry


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = navController()

        val bottomNavView = binding.bottomNavigation
        bottomNavView.setupWithNavController(navController)
        bottomNavView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_UNLABELED

        navController.addOnDestinationChangedListener { _, _, arguments ->
            bottomNavView.isVisible = arguments?.getBoolean("ShowBottomNav", false) == true
        }

        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installCheckProviderFactory()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                throw Exception("This app uses Sentry! :)")
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }
    }

    override fun onSupportNavigateUp() = navController().navigateUp() || super.onSupportNavigateUp()

    private fun navController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}