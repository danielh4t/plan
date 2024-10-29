package app.stacq.plan.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.stacq.plan.R
import app.stacq.plan.domain.Task


@Composable
fun TaskTextField(
    task: Task? = null,
    onSave: (String, String) -> Unit,
) {
    // Manage focus
    val focusManager = LocalFocusManager.current

    var name by rememberSaveable { mutableStateOf(task?.name ?: "") }
    var notes by rememberSaveable { mutableStateOf(task?.notes ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        BasicTextField(
            value = name,
            onValueChange = { name = it },
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next  // Next action for name
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }  // Moves focus to notes
            ),
            decorationBox = { innerTextField ->
                if (name.isBlank()) {
                    Text(
                        text = stringResource(R.string.task_name),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
                innerTextField()
            },

            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // Space between Name and Notes

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Limited height to make it scrollable
                .verticalScroll(rememberScrollState())
        ) {
            BasicTextField(
                value = notes,
                onValueChange = { notes = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    if (notes.isBlank()) {
                        Text(
                            text = stringResource(R.string.task_notes),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(

                onClick = { onSave(name, notes) }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.content_save_check)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Preview
@Composable
fun TaskTextFieldPreview() {
    TaskTextField(
        onSave = { _, _ -> }
    )
}
