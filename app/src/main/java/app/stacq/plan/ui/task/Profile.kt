package app.stacq.plan.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.stacq.plan.R
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    onProfileClick: () -> Unit = {},
) {
    if (imageUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.profile_picture),
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() },
            contentScale = ContentScale.Crop,
        )
    } else {
        IconButton(
            onClick = { onProfileClick() },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.content_account_circle),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}