package com.abdullahhalis.overlai.utils

import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun String.toCapitalizeFirstChar(): String = lowercase().replaceFirstChar { it.uppercase() }

fun List<TranslationHistoryEntity>.groupByDate(): Map<String, List<TranslationHistoryEntity>> {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterday = today - 24 * 60 * 60 * 1000
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    return groupBy { entity ->
        when {
            entity.timestamp >= today -> "Today"
            entity.timestamp >= yesterday -> "Yesterday"
            else -> dateFormat.format(Date(entity.timestamp))
        }
    }
}