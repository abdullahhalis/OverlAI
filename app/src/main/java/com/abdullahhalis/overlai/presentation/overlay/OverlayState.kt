package com.abdullahhalis.overlai.presentation.overlay

import com.abdullahhalis.overlai.data.model.TranslationResult

sealed class OverlayState {
    object Idle: OverlayState()
    object Loading: OverlayState()
    data class Success(val results: List<TranslationResult>): OverlayState()
    data class Error(val message: String): OverlayState()
}