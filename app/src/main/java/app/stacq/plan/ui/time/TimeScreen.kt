package app.stacq.plan.ui.time


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.stacq.plan.data.AppViewModelProvider
import app.stacq.plan.domain.Task
import app.stacq.plan.ui.theme.PlanTheme
import app.stacq.plan.utility.TimeFormatter
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeScreen(
    viewModel: TimeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is TimeUIState.Error) {
            val message = (uiState as TimeUIState.Error).errorMessage
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        when (uiState) {
            is TimeUIState.Success -> {
                val groupedTasks = (uiState as TimeUIState.Success).groupedTasks

                TimeContent(
                    modifier = Modifier.consumeWindowInsets(innerPadding),
                    groupedTasks = groupedTasks
                )
            }

            else -> { // Initial or Loading state
                Column(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun TimeScreenPreview() {
    val tasks = listOf(
        Task(
            id = "a",
            name = "Design UI",
            notes = "Create wireframes",
            createdAt = 1732270512L,
            completedAt = 1732274112L
        ),
        Task(
            id = "b",
            name = "Develop Backend",
            notes = null,
            createdAt = 1732356912L,
            completedAt = 1732357912L
        ),
        Task(
            id = "c",
            name = "Write Documentation",
            notes = "API Docs",
            createdAt = 1732443312L,
            completedAt = 1732446912L
        ),
        Task(
            id = "d",
            name = "Set Up CI/CD",
            notes = null,
            createdAt = 1732529712L,
            completedAt = 1732533312L
        ),
        Task(
            id = "e",
            name = "Conduct Testing",
            notes = "Unit and Integration Tests",
            createdAt = 1732616112L,
            completedAt = null
        )
    )

    val formattedTasks = tasks.map { task ->
        formatTaskToTaskTime(task)
    }

    val groupedTasks = formattedTasks.groupBy {
        TimeFormatter.formatDate(it.createdAt)
    }

    PlanTheme {
        TimeContent(groupedTasks = groupedTasks)
    }
}
