package app.stacq.plan.ui.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import app.stacq.plan.data.AppViewModelProvider
import app.stacq.plan.ui.theme.PlanTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    handleSignOut: () -> Unit = {},
    handleTime: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is TaskUIState.Error) {
            val message = (uiState as TaskUIState.Error).errorMessage
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        TaskContent(
            uiState = uiState,
            modifier = Modifier.consumeWindowInsets(innerPadding),
            handleSignOut = handleSignOut,
            handleTime = handleTime,
            onShowBottomSheet = { viewModel.showBottomSheet() },
            onDismissBottomSheet = { viewModel.dismissBottomSheet() },
            onSaveTask = { name, notes -> viewModel.saveTask(name, notes) },
            onCompleteTask = { task -> viewModel.complete(task) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TaskScreenPreview() {
    PlanTheme {
        TaskContent(
            uiState = TaskUIState.Success(
                profileImageUrl = null,
                showBottomSheet = false,
                task = null
            ),
            handleSignOut = {},
            handleTime = {},
            onShowBottomSheet = {},
            onDismissBottomSheet = {},
            onSaveTask = { _, _ -> },
            onCompleteTask = {}
        )
    }
}
