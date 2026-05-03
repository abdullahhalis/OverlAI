package com.abdullahhalis.overlai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdullahhalis.overlai.data.local.dao.TranslationHistoryDao
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity

@Database(
    entities = [TranslationHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationHistoryDao(): TranslationHistoryDao
}