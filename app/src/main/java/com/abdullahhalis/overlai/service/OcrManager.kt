package com.abdullahhalis.overlai.service

import android.graphics.Bitmap
import com.abdullahhalis.overlai.data.model.OcrResult
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.mergeNearbyBlocks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class OcrManager @Inject constructor() {

    private val recognizers = mapOf(
        OcrLanguage.ENGLISH to TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS),
        OcrLanguage.JAPANESE to TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build()),
        OcrLanguage.KOREAN to TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build()),
        OcrLanguage.CHINESE to TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build()),
    )

    suspend fun recognize(bitmap: Bitmap, language: OcrLanguage = OcrLanguage.ENGLISH): List<OcrResult> {
        val recognizer = recognizers[language] ?: recognizers[OcrLanguage.ENGLISH]!!
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { continuation ->
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val result = visionText.textBlocks.map { block ->
                        OcrResult(
                            text = block.text,
                            boundingBox = block.boundingBox
                        )
                    }.mergeNearbyBlocks(language)
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}