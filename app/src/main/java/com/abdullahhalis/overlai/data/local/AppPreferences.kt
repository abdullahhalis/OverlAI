package com.abdullahhalis.overlai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class AppPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    val sourceLanguage: Flow<OcrLanguage> = context.dataStore.data.map { prefs ->
        val value = prefs[KEY_SOURCE_LANGUAGE] ?: OcrLanguage.ENGLISH.name
        OcrLanguage.valueOf(value)
    }

    val targetLanguage: Flow<TranslationLanguage> = context.dataStore.data.map { prefs ->
        val value = prefs[KEY_TARGET_LANGUAGE] ?: TranslationLanguage.INDONESIAN.name
        TranslationLanguage.valueOf(value)
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setSourceLanguage(language: OcrLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SOURCE_LANGUAGE] = language.name
        }
    }

    suspend fun setTargetLanguage(language: TranslationLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TARGET_LANGUAGE] = language.name
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    companion object{
        private val KEY_SOURCE_LANGUAGE = stringPreferencesKey("source_language")
        private val KEY_TARGET_LANGUAGE = stringPreferencesKey("target_language")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}