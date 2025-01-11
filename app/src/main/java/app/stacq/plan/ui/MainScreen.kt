package app.stacq.plan.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.stacq.plan.ui.auth.AuthScreen
import app.stacq.plan.ui.task.TaskScreen
import app.stacq.plan.ui.time.TimeScreen


enum class Screens {
    Auth,
    Task,
    Time
}

@Preview
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isAuthenticated by viewModel.isUserAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated && navController.currentDestination?.route != Screens.Auth.name) {
            navController.navigate(Screens.Auth.name) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screens.Task.name else Screens.Auth.name,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = Screens.Auth.name) {
                AuthScreen(
                    handleSignIn = {
                        navController.navigate(Screens.Task.name)
                    }
                )
            }
            composable(route = Screens.Task.name) {
                TaskScreen(
                    handleSignOut = {
                        viewModel.signOut()
                    },
                    handleTime = {
                        navController.navigate(Screens.Time.name)
                    }
                )
            }
            composable(route = Screens.Time.name) {
                TimeScreen()
            }
        }
    }
}
