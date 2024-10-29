package app.stacq.plan.ui.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.stacq.plan.R
import app.stacq.plan.data.AppViewModelProvider
import app.stacq.plan.ui.theme.PlanTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
    handleSignOut: () -> Unit = {},
) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is TaskUIState.Error -> {
                val message = (uiState as TaskUIState.Error).errorMessage
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            when (uiState) {
                is TaskUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

                is TaskUIState.Success -> {
                    val imageUrl = (uiState as TaskUIState.Success).profileImageUrl
                    val showBottomSheet = (uiState as TaskUIState.Success).showBottomSheet
                    val task = (uiState as TaskUIState.Success).task

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Profile(
                            imageUrl = imageUrl,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 16.dp, end = 16.dp),
                            onProfileClick = handleSignOut
                        )

                        if (showBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    viewModel.dismissBottomSheet()
                                },
                                sheetState = sheetState
                            ) {
                                // Sheet content
                                TaskTextField(
                                    task = task,
                                    onSave = { name, notes -> viewModel.saveTask(name, notes) }
                                )
                            }
                        }
                        if (task == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.showBottomSheet() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.content_add) // Optional: Provide a content description for accessibility
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = task.name,
                                    fontSize = 36.sp,
                                    lineHeight = 36.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(32.dp)
                                        .combinedClickable(
                                            onClick = { viewModel.showBottomSheet() },
                                            onLongClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.complete(task)
                                            },
                                            onLongClickLabel = stringResource(R.string.complete)
                                        )
                                )
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}


@Composable
@Preview
fun TaskScreenPreview() {
    PlanTheme {
        TaskScreen()
    }
}
