package com.abdullahhalis.overlai.utils

import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity

val dummyHistory = listOf(
    TranslationHistoryEntity(
        id = 1,
        originalText = "ありがとう",
        translatedText = "Terima kasih",
        sourceLanguage = "JAPANESE",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis()
    ),
    TranslationHistoryEntity(
        id = 2,
        originalText = "危ない、気をつけて！",
        translatedText = "Berbahaya, hati-hati!",
        sourceLanguage = "JAPANESE",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis()
    ),
    TranslationHistoryEntity(
        id = 3,
        originalText = "나는 학교에 간다",
        translatedText = "Saya pergi ke sekolah",
        sourceLanguage = "KOREAN",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis() - 86400000 // yesterday
    ),
    TranslationHistoryEntity(
        id = 4,
        originalText = "정말 대단해!",
        translatedText = "Sungguh luar biasa!",
        sourceLanguage = "KOREAN",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis() - 86400000
    ),
    TranslationHistoryEntity(
        id = 5,
        originalText = "我爱你",
        translatedText = "Aku mencintaimu",
        sourceLanguage = "CHINESE",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis() - 86400000 * 2 // 2 days ago
    ),
    TranslationHistoryEntity(
        id = 6,
        originalText = "今日はいい天気ですね",
        translatedText = "Cuaca hari ini bagus ya",
        sourceLanguage = "JAPANESE",
        targetLanguage = "INDONESIAN",
        timestamp = System.currentTimeMillis() - 86400000 * 5
    ),
    TranslationHistoryEntity(
        id = 7,
        originalText = "どこへ行くの？",
        translatedText = "Mau pergi ke mana?",
        sourceLanguage = "JAPANESE",
        targetLanguage = "ENGLISH",
        timestamp = System.currentTimeMillis() - 86400000 * 5
    ),
)