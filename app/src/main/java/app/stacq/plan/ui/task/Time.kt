package app.stacq.plan.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.stacq.plan.R
import app.stacq.plan.ui.theme.PlanTheme


@Composable
fun Title(
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {},
) {
    TextButton(
        onClick = { onTitleClick() },
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.app_name)
        )
    }
}


@Composable
@Preview(showBackground = true)
fun TimePreview() {
    PlanTheme {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Title(
                onTitleClick = {}
            )
        }
    }
}
