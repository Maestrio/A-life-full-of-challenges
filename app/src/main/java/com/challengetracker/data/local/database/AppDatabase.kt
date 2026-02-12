package com.challengetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.challengetracker.data.local.database.dao.ChallengeDao
import com.challengetracker.data.local.database.dao.DailyCheckInDao
import com.challengetracker.data.local.database.dao.WeeklyReflectionDao
import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity

@Database(
    entities = [
        ChallengeEntity::class,
        DailyCheckInEntity::class,
        WeeklyReflectionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun weeklyReflectionDao(): WeeklyReflectionDao
}
