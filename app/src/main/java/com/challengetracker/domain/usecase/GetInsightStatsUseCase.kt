package com.challengetracker.domain.usecase

import com.challengetracker.data.local.database.entities.ChallengeStatus
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.domain.model.ChallengeWithStats
import com.challengetracker.domain.model.InsightStats
import javax.inject.Inject

class GetInsightStatsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(): InsightStats {
        val challenges = challengeRepository.getAllChallengesOnce()
        val allCheckIns = checkInRepository.getAllCheckInsOnce()

        if (challenges.isEmpty()) return InsightStats()

        val completed = challenges.filter { it.status == ChallengeStatus.COMPLETED }
        val archived = challenges.filter { it.status == ChallengeStatus.ARCHIVED }

        val challengeStats = challenges.map { challenge ->
            val checkIns = allCheckIns.filter { it.challengeId == challenge.id }
            ChallengeWithStats.calculate(challenge, checkIns)
        }

        val best = challengeStats.maxByOrNull { it.successRate }
        val worst = challengeStats.filter { it.totalTrackedDays > 0 }.minByOrNull { it.successRate }

        val totalTrackedDays = allCheckIns.size
        val totalCompleted = allCheckIns.count { it.completed }
        val overallRate = if (totalTrackedDays > 0) totalCompleted.toFloat() / totalTrackedDays * 100f else 0f

        val finishedCount = completed.size + archived.size
        val completionRate = if (finishedCount > 0) completed.size.toFloat() / finishedCount * 100f else 0f

        return InsightStats(
            totalChallengesCreated = challenges.size,
            totalChallengesCompleted = completed.size,
            totalChallengesAbandoned = archived.size,
            totalDaysTracked = totalTrackedDays,
            overallSuccessRate = overallRate,
            bestChallengeName = best?.challenge?.name ?: "",
            bestChallengeRate = best?.successRate ?: 0f,
            mostStruggledName = worst?.challenge?.name ?: "",
            mostStruggledRate = worst?.successRate ?: 0f,
            longestStreakEver = challengeStats.maxOfOrNull { it.longestStreak } ?: 0,
            completionRate = completionRate
        )
    }
}
