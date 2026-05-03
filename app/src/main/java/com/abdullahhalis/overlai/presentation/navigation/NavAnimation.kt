package com.abdullahhalis.overlai.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

object NavAnimation {
    private const val FORWARD_DURATION = 300
    private const val BACK_DURATION = 250
    val enter : AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(FORWARD_DURATION)
        ) + fadeIn(
            animationSpec = tween(FORWARD_DURATION)
        )
    }

    val exit : AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(FORWARD_DURATION)
        ) + fadeOut(
            tween(FORWARD_DURATION)
        )
    }

    val popEnter : AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(BACK_DURATION)
        ) + fadeIn(
            tween(BACK_DURATION)
        )
    }

    val popExit : AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(BACK_DURATION)
        ) + fadeOut(
            tween(BACK_DURATION)
        )
    }
}