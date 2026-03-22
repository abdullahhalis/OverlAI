package com.abdullahhalis.overlai.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahhalis.overlai.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    val isOnboardingCompleted = appRepository.isOnboardingCompleted
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    init {
        viewModelScope.launch {
            appRepository.sourceLanguage.first()
            appRepository.targetLanguage.first()
            appRepository.isOnboardingCompleted.first()
            _isReady.value = true
        }
    }
}