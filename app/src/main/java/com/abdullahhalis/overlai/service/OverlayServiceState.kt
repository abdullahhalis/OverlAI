package com.abdullahhalis.overlai.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayServiceState @Inject constructor() {
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing = _isCapturing.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating = _isTranslating.asStateFlow()

    fun setRunning(value: Boolean) {
        _isRunning.value = value
    }

    fun setCapturing(value: Boolean) {
        _isCapturing.value = value
    }

    fun setTranslating(value: Boolean) {
        _isTranslating.value = value
    }

}