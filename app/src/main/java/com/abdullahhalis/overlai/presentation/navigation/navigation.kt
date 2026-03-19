package com.abdullahhalis.overlai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdullahhalis.overlai.presentation.main.MainScreen

sealed class Screen(val route: String) {
    object Main: Screen("main")
}

@Composable
fun NavGraph(
    isOverlayRunning: Boolean,
    onStartOverlay: () -> Unit,
    onStopOverlay: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                isOverlayRunning,
                onStartOverlay,
                onStopOverlay,
                modifier
            )
        }
    }
}