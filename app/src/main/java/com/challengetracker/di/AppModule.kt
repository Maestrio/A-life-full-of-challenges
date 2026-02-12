package com.challengetracker.di

import android.content.Context
import androidx.room.Room
import com.challengetracker.data.local.database.AppDatabase
import com.challengetracker.data.local.database.dao.ChallengeDao
import com.challengetracker.data.local.database.dao.DailyCheckInDao
import com.challengetracker.data.local.database.dao.WeeklyReflectionDao
import com.challengetracker.data.local.datastore.SettingsDataStore
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "challenge_tracker_db"
        ).build()
    }

    @Provides
    fun provideChallengeDao(database: AppDatabase): ChallengeDao = database.challengeDao()

    @Provides
    fun provideDailyCheckInDao(database: AppDatabase): DailyCheckInDao = database.dailyCheckInDao()

    @Provides
    fun provideWeeklyReflectionDao(database: AppDatabase): WeeklyReflectionDao = database.weeklyReflectionDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
}
