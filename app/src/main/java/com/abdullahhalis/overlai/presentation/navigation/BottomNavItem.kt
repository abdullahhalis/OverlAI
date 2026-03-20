package com.abdullahhalis.overlai.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home: BottomNavItem(
        route = Screen.Main.route,
        label = "Home",
        icon = Icons.Outlined.Home
    )
    object History: BottomNavItem(
        route = Screen.History.route,
        label = "History",
        icon = Icons.Outlined.History
    )
}