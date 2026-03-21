package com.abdullahhalis.overlai.utils

import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity

sealed class HistoryListItem {
    data class Item(val entity: TranslationHistoryEntity): HistoryListItem()
    data class Separator(val label: String): HistoryListItem()
}