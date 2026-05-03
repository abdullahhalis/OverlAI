package com.abdullahhalis.overlai.data.model

import android.graphics.Rect

data class OcrResult (
    val text: String,
    val boundingBox: Rect?
)