package com.challengetracker.domain.model

import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.ChallengeType
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class ChallengeWithStats(
    val challenge: ChallengeEntity,
    val checkIns: List<DailyCheckInEntity> = emptyList(),
    val completedDays: Int = 0,
    val totalTrackedDays: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val successRate: Float = 0f,
    val daysElapsed: Int = 0,
    val daysRemaining: Int = 0,
    val projectedFinalRate: Float = 0f,
    val todayCheckedIn: Boolean = false,
    val todaySkipped: Boolean = false
) {
    val isOnTrack: Boolean
        get() = successRate >= challenge.successThreshold

    val progressFraction: Float
        get() = if (challenge.duration > 0) daysElapsed.toFloat() / challenge.duration else 0f

    companion object {
        fun calculate(challenge: ChallengeEntity, checkIns: List<DailyCheckInEntity>): ChallengeWithStats {
            val today = LocalDate.now()
            val daysElapsed = ChronoUnit.DAYS.between(challenge.startDate, today).toInt().coerceIn(0, challenge.duration)
            val daysRemaining = (challenge.duration - daysElapsed).coerceAtLeast(0)

            val completedDays = checkIns.count { it.completed }
            val totalTrackedDays = checkIns.count { !it.skipped }
            val successRate = if (totalTrackedDays > 0) completedDays.toFloat() / totalTrackedDays * 100f else 0f

            val todayCheckIn = checkIns.find { it.date == today }

            // Calculate streaks
            val sortedCheckIns = checkIns.filter { !it.skipped }.sortedByDescending { it.date }
            var currentStreak = 0
            var longestStreak = 0
            var tempStreak = 0

            val allCheckInsSorted = checkIns.filter { !it.skipped }.sortedBy { it.date }
            for (checkIn in allCheckInsSorted) {
                if (checkIn.completed) {
                    tempStreak++
                    longestStreak = maxOf(longestStreak, tempStreak)
                } else {
                    tempStreak = 0
                }
            }

            // Current streak (from today backwards)
            for (checkIn in sortedCheckIns) {
                if (checkIn.completed) {
                    currentStreak++
                } else {
                    break
                }
            }

            // Projected final rate
            val projectedFinalRate = if (daysElapsed > 0) {
                val dailyRate = completedDays.toFloat() / daysElapsed
                dailyRate * challenge.duration / challenge.duration * 100f
            } else successRate

            return ChallengeWithStats(
                challenge = challenge,
                checkIns = checkIns,
                completedDays = completedDays,
                totalTrackedDays = totalTrackedDays,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                successRate = successRate,
                daysElapsed = daysElapsed,
                daysRemaining = daysRemaining,
                projectedFinalRate = projectedFinalRate,
                todayCheckedIn = todayCheckIn?.completed == true,
                todaySkipped = todayCheckIn?.skipped == true
            )
        }
    }
}
