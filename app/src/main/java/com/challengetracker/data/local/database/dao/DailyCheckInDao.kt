package com.challengetracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyCheckInDao {
    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId ORDER BY date ASC")
    fun getCheckInsForChallenge(challengeId: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId AND date = :date LIMIT 1")
    fun getCheckInForDate(challengeId: String, date: LocalDate): Flow<DailyCheckInEntity?>

    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId AND date = :date LIMIT 1")
    suspend fun getCheckInForDateOnce(challengeId: String, date: LocalDate): DailyCheckInEntity?

    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId AND completed = 1 ORDER BY date ASC")
    fun getCompletedCheckIns(challengeId: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId AND completed = 0 AND skipped = 0 ORDER BY date ASC")
    fun getFailedCheckIns(challengeId: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT COUNT(*) FROM daily_checkins WHERE challengeId = :challengeId AND completed = 1")
    fun getCompletedCount(challengeId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_checkins WHERE challengeId = :challengeId AND (completed = 1 OR skipped = 1)")
    fun getCompletedOrSkippedCount(challengeId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_checkins WHERE challengeId = :challengeId")
    fun getTotalCheckInCount(challengeId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: DailyCheckInEntity)

    @Update
    suspend fun updateCheckIn(checkIn: DailyCheckInEntity)

    @Query("DELETE FROM daily_checkins WHERE challengeId = :challengeId")
    suspend fun deleteCheckInsForChallenge(challengeId: String)

    @Query("SELECT * FROM daily_checkins WHERE challengeId = :challengeId")
    suspend fun getCheckInsForChallengeOnce(challengeId: String): List<DailyCheckInEntity>

    @Query("SELECT * FROM daily_checkins")
    suspend fun getAllCheckInsOnce(): List<DailyCheckInEntity>
}
