package com.abdullahhalis.overlai.presentation.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ScreenShare
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage (
    val icon: ImageVector,
    val title: String,
    val description: String
)

val onboardingPage = listOf(
    OnboardingPage(
        icon = Icons.Outlined.TouchApp,
        title = "Translate Anything \nOn Your Screen",
        description = "OverlAI adds a floating bubble on top of any app. Tap it to instantly translate text from manga, manhwa, or webtoons — without switching apps."
    ),
    OnboardingPage(
        icon = Icons.AutoMirrored.Outlined.ScreenShare,
        title = "Display Over\nOther Apps",
        description = "OverlAI needs permission to show the floating bubble and translation results on top of other apps. You'll be directed to Settings to enable this."
    ),
    OnboardingPage(
        icon = Icons.Outlined.Translate,
        title = "Screen Capture\nPermission",
        description = "To detect text, OverlAI captures your screen when you tap the bubble. Your screen is only captured when you tap — never in the background."
    )
)