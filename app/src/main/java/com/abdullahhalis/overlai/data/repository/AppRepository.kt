package com.abdullahhalis.overlai.data.repository

import com.abdullahhalis.overlai.data.local.AppPreferences
import com.abdullahhalis.overlai.data.local.dao.TranslationHistoryDao
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val appPreferences: AppPreferences,
    private val dao: TranslationHistoryDao
) {
    val sourceLanguage: Flow<OcrLanguage> = appPreferences.sourceLanguage
    val targetLanguage: Flow<TranslationLanguage> = appPreferences.targetLanguage

    suspend fun setSourceLanguage(language: OcrLanguage) {
        appPreferences.setSourceLanguage(language)
    }

    suspend fun setTargetLanguage(language: TranslationLanguage) {
        appPreferences.setTargetLanguage(language)
    }

    fun getHistory(): Flow<List<TranslationHistoryEntity>> = dao.getAll()

    suspend fun insertHistory(entity: TranslationHistoryEntity) = dao.insert(entity)

    suspend fun deleteHistoryById(id: Long) = dao.deleteHistoryById(id)

    suspend fun deleteAllHistory() = dao.deleteAllHistory()
}