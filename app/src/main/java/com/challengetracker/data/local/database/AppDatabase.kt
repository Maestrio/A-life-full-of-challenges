package com.challengetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun weeklyReflectionDao(): WeeklyReflectionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE challenges ADD COLUMN challengeMode TEXT NOT NULL DEFAULT 'MULTI_DAY'")
            }
        }
    }
}
