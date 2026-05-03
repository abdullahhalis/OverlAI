package com.abdullahhalis.overlai.data.model

import android.graphics.Rect

data class TranslationResult (
    val originalText: String,
    val translatedText: String,
    val boundingBox: Rect?
)