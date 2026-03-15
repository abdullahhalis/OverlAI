package com.abdullahhalis.overlai.data.repository

import com.abdullahhalis.overlai.data.local.AppPreferences
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val appPreferences: AppPreferences
) {
    val sourceLanguage: Flow<OcrLanguage> = appPreferences.sourceLanguage
    val targetLanguage: Flow<TranslationLanguage> = appPreferences.targetLanguage

    suspend fun setSourceLanguage(language: OcrLanguage) {
        appPreferences.setSourceLanguage(language)
    }

    suspend fun setTargetLanguage(language: TranslationLanguage) {
        appPreferences.setTargetLanguage(language)
    }
}