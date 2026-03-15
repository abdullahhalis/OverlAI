package com.abdullahhalis.overlai.presentation.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdullahhalis.overlai.data.model.TranslationResult
import com.abdullahhalis.overlai.presentation.overlay.component.ErrorOverlay
import com.abdullahhalis.overlai.presentation.overlay.component.LoadingOverlay

@Composable
fun TranslationOverlay(
    state: OverlayState,
    topOffset: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is OverlayState.Idle  -> {}
        is OverlayState.Loading -> {
            LoadingOverlay()
        }
        is OverlayState.Success -> {
            ResultOverlay(
                results = state.results,
                topOffset = topOffset,
                onDismiss = onDismiss,
                modifier
            )
        }
        is OverlayState.Error -> {
            ErrorOverlay(
                message = state.message,
                onDismiss = onDismiss,
                modifier
            )
        }
    }
}

@Composable
private fun ResultOverlay(
    results: List<TranslationResult>,
    topOffset: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onDismiss() }
    ) {
        results.forEach { result ->
            if (result.boundingBox != null && result.translatedText.isNotBlank()) {
                TranslationBubble(result, topOffset)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tap anywhere to dismiss",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TranslationBubble(
    result: TranslationResult,
    topOffset: Int,
    modifier: Modifier = Modifier
) {
    val box = result.boundingBox!!
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val leftDp = with(density) { box.left.toDp() }
    val topDp = with(density) { box.top.toDp() }
    val topOffsetDp = with(density) { topOffset.toDp()}
    val widthDp = with(density) { box.width().toDp() }

    val isVertical = box.height() > box.width() * 1.5f

    val clampedLeft = leftDp.coerceIn(0.dp, (screenWidth - widthDp).coerceAtLeast(0.dp))
    val clampedTop = (topDp - topOffsetDp).coerceIn(0.dp, screenHeight - 40.dp)

    Box(
        modifier = modifier
            .absoluteOffset(x = clampedLeft, y = clampedTop)
            .width(widthDp.coerceAtLeast(if (isVertical) 40.dp else 60.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = result.translatedText,
            color = Color.White,
            fontSize = if (isVertical) 8.sp else 10.sp,
            lineHeight = if (isVertical) 11.sp else 13.sp,
            textAlign = TextAlign.Center,
            maxLines = if (isVertical) 6 else 3,
            overflow = TextOverflow.Visible
        )
    }
}