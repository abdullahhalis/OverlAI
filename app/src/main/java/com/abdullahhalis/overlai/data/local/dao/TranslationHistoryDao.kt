package com.abdullahhalis.overlai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TranslationHistoryEntity)

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranslationHistoryEntity>>

    @Query("DELETE FROM translation_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM translation_history")
    suspend fun deleteAllHistory()
}