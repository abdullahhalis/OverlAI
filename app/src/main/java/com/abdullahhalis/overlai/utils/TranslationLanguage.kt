package com.abdullahhalis.overlai.utils

import com.google.mlkit.nl.translate.TranslateLanguage

enum class TranslationLanguage(val code: String) {
    INDONESIAN(TranslateLanguage.INDONESIAN),
    ENGLISH(TranslateLanguage.ENGLISH);

    companion object {
        fun default() = INDONESIAN
    }
}