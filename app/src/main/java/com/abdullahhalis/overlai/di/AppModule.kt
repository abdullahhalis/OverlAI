package com.abdullahhalis.overlai.di

import android.content.Context
import androidx.room.Room
import com.abdullahhalis.overlai.data.local.AppDatabase
import com.abdullahhalis.overlai.data.local.dao.TranslationHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "overlai_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTranslationHistoryDao(db: AppDatabase): TranslationHistoryDao = db.translationHistoryDao()
}