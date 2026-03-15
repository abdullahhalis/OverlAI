package com.abdullahhalis.overlai.utils

import com.google.mlkit.nl.translate.TranslateLanguage

enum class OcrLanguage(val code: String) {
    LATIN(TranslateLanguage.ENGLISH),
    JAPANESE(TranslateLanguage.JAPANESE),
    KOREAN(TranslateLanguage.KOREAN),
    CHINESE(TranslateLanguage.CHINESE);

    companion object {
        fun default() = LATIN
    }
}