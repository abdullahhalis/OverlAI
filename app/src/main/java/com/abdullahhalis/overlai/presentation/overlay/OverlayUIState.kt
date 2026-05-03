package com.abdullahhalis.overlai.presentation.overlay

import com.abdullahhalis.overlai.data.model.TranslationResult

sealed class OverlayUIState {
    object Idle: OverlayUIState()
    object Loading: OverlayUIState()
    data class Success(val results: List<TranslationResult>): OverlayUIState()
    data class Error(val message: String): OverlayUIState()
}