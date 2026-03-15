package com.abdullahhalis.overlai.service

import android.util.Log
import com.abdullahhalis.overlai.data.model.OcrResult
import com.abdullahhalis.overlai.data.model.TranslationResult
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TranslationManager @Inject constructor() {

    private val translatorCache = mutableMapOf<String, Translator>()

    private fun getTranslator(
        sourceLanguage: String,
        targetLanguage: String
    ): Translator {
        val key = "$sourceLanguage-$targetLanguage"
        return translatorCache.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            Translation.getClient(options)
        }
    }

    suspend fun translate(
        ocrResults: List<OcrResult>,
        sourceLanguage: OcrLanguage = OcrLanguage.default(),
        targetLanguage: TranslationLanguage = TranslationLanguage.default()
    ): List<TranslationResult> {
        val translator = getTranslator(sourceLanguage.code, targetLanguage.code)

        translator.downloadModelIfNeeded().await()

        return ocrResults.map { ocrResult ->
            suspendCancellableCoroutine { continuation ->
                translator.translate(ocrResult.text)
                    .addOnSuccessListener { translatedText ->
                        continuation.resume(
                            TranslationResult(
                                originalText = ocrResult.text,
                                translatedText = translatedText,
                                boundingBox = ocrResult.boundingBox
                            )
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.e(TranslationManager::class.java.simpleName, "Translation failed: ${e.message}")
                        continuation.resume(
                            TranslationResult(
                                ocrResult.text,
                                ocrResult.text,
                                ocrResult.boundingBox
                            )
                        )
                    }
            }
        }
    }
}