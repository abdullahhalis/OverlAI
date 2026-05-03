package com.abdullahhalis.overlai.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahhalis.overlai.data.repository.AppRepository
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    val sourceLanguage = appRepository.sourceLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OcrLanguage.ENGLISH
    )

    val targetLanguage = appRepository.targetLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TranslationLanguage.INDONESIAN
    )

    fun setSourceLanguage(language: OcrLanguage) {
        viewModelScope.launch {
            appRepository.setSourceLanguage(language)
        }
    }

    fun setTargetLanguage(language: TranslationLanguage) {
        viewModelScope.launch {
            appRepository.setTargetLanguage(language)
        }
    }
}