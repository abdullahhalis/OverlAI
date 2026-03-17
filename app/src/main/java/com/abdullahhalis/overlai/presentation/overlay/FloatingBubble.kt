package com.abdullahhalis.overlai.presentation.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdullahhalis.overlai.R
import com.abdullahhalis.overlai.presentation.ui.theme.OverlAITheme


@Composable
fun FloatingBubble(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_overlai),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .scale(1.6f)
            .alpha(0.85f),
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
private fun FloatPrev() {
    OverlAITheme(darkTheme = true) {
        FloatingBubble()
    }
}