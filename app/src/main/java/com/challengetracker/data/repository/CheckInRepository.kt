package com.challengetracker.data.repository

import com.challengetracker.data.local.database.dao.DailyCheckInDao
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepository @Inject constructor(
    private val checkInDao: DailyCheckInDao
) {
    fun getCheckInsForChallenge(challengeId: String): Flow<List<DailyCheckInEntity>> =
        checkInDao.getCheckInsForChallenge(challengeId)

    fun getCheckInForDate(challengeId: String, date: LocalDate): Flow<DailyCheckInEntity?> =
        checkInDao.getCheckInForDate(challengeId, date)

    fun getCompletedCheckIns(challengeId: String): Flow<List<DailyCheckInEntity>> =
        checkInDao.getCompletedCheckIns(challengeId)

    fun getFailedCheckIns(challengeId: String): Flow<List<DailyCheckInEntity>> =
        checkInDao.getFailedCheckIns(challengeId)

    fun getCompletedCount(challengeId: String): Flow<Int> =
        checkInDao.getCompletedCount(challengeId)

    fun getTotalCheckInCount(challengeId: String): Flow<Int> =
        checkInDao.getTotalCheckInCount(challengeId)

    suspend fun checkIn(
        challengeId: String,
        date: LocalDate,
        completed: Boolean,
        skipped: Boolean = false,
        note: String = "",
        mood: Int? = null,
        difficulty: Int? = null
    ) {
        val existing = checkInDao.getCheckInForDateOnce(challengeId, date)
        if (existing != null) {
            checkInDao.updateCheckIn(
                existing.copy(
                    completed = completed,
                    skipped = skipped,
                    note = note,
                    mood = mood,
                    difficulty = difficulty,
                    modifiedAt = LocalDateTime.now(),
                    wasEdited = true
                )
            )
        } else {
            checkInDao.insertCheckIn(
                DailyCheckInEntity(
                    challengeId = challengeId,
                    date = date,
                    completed = completed,
                    skipped = skipped,
                    note = note,
                    mood = mood,
                    difficulty = difficulty
                )
            )
        }
    }

    suspend fun editCheckIn(
        checkIn: DailyCheckInEntity,
        completed: Boolean,
        note: String,
        mood: Int?,
        difficulty: Int?,
        editReason: String
    ) {
        checkInDao.updateCheckIn(
            checkIn.copy(
                completed = completed,
                note = note,
                mood = mood,
                difficulty = difficulty,
                modifiedAt = LocalDateTime.now(),
                wasEdited = true,
                editReason = editReason
            )
        )
    }

    suspend fun getAllCheckInsOnce(): List<DailyCheckInEntity> = checkInDao.getAllCheckInsOnce()

    suspend fun getCheckInsForChallengeOnce(challengeId: String): List<DailyCheckInEntity> =
        checkInDao.getCheckInsForChallengeOnce(challengeId)
}
