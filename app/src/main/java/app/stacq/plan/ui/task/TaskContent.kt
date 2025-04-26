package app.stacq.plan.ui.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.stacq.plan.R
import app.stacq.plan.domain.Task


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskContent(
    uiState: TaskUIState,
    modifier: Modifier = Modifier,
    handleSignOut: () -> Unit,
    handleTime: () -> Unit,
    onShowBottomSheet: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onSaveTask: (String, String?) -> Unit,
    onCompleteTask: (Task) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is TaskUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            is TaskUIState.Success -> {
                val imageUrl = uiState.profileImageUrl
                val showBottomSheet = uiState.showBottomSheet
                val task = uiState.task

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Title(
                            onTitleClick = handleTime,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                        Profile(
                            imageUrl = imageUrl,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp),
                            onProfileClick = handleSignOut
                        )
                    }
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = onDismissBottomSheet,
                            sheetState = sheetState,
                        ) {
                            TaskTextField(
                                task = task,
                                onSave = onSaveTask
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
                            FilledTonalIconButton(onClick = onShowBottomSheet) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.content_add)
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
                                        onClick = onShowBottomSheet,
                                        onLongClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onCompleteTask(task)
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

