package com.abdullahhalis.overlai.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abdullahhalis.overlai.presentation.history.HistoryScreen
import com.abdullahhalis.overlai.presentation.main.MainScreen
import com.abdullahhalis.overlai.presentation.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    object Onboarding: Screen("onboarding")
    object Main: Screen("main")
    object History: Screen("history")
}

@Composable
fun NavGraph(
    isOnboardingComplete: Boolean,
    isOverlayRunning: Boolean,
    onStartOverlay: () -> Unit,
    onStopOverlay: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val bottomNavItem = listOf(BottomNavItem.Home, BottomNavItem.History)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (isOnboardingComplete)
        Screen.Main.route
    else
        Screen.Onboarding.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Onboarding.route) {
                NavigationBar {
                    bottomNavItem.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Main.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,

                                ),
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding),
            enterTransition = NavAnimation.enter,
            exitTransition = NavAnimation.exit,
            popEnterTransition = NavAnimation.popEnter,
            popExitTransition = NavAnimation.popExit
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Main.route) {
                MainScreen(
                    isOverlayRunning,
                    onStartOverlay,
                    onStopOverlay,
                    modifier
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    navigateToMain = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}