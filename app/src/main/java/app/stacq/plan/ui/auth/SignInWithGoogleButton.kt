package app.stacq.plan.ui.auth

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.stacq.plan.R


@Composable
fun SignInWithGoogleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    borderColor: Color = Color.LightGray,
    backgroundColor: Color =  MaterialTheme.colorScheme.background,
) {
    Surface(
        modifier = modifier.clickable(

            onClick = onClick
        ),
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription =  stringResource(R.string.content_sign_in_button),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text( text = stringResource(R.string.sign_in_with_google))

        }
    }
}

@Composable
@Preview
fun SignInWithGoogleButtonPreview() {
    SignInWithGoogleButton(
        onClick = { }
    )
}