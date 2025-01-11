package app.stacq.plan.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.stacq.plan.R
import app.stacq.plan.ui.theme.PlanTheme


@Composable
fun Time(
    modifier: Modifier = Modifier,
    onTimeClick: () -> Unit = {},
) {

    IconButton(
        onClick = { onTimeClick() },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(R.string.content_account_circle),
            modifier = Modifier.size(40.dp)
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
            horizontalArrangement = Arrangement.Start
        ) {
            Time(
                onTimeClick = {}
            )
        }
    }
}
