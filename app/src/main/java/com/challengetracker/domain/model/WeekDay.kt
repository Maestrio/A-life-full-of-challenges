package com.challengetracker.domain.model

data class DayOfWeekStats(
    val dayOfWeek: java.time.DayOfWeek,
    val completedCount: Int,
    val totalCount: Int,
    val successRate: Float
)

data class InsightStats(
    val totalChallengesCreated: Int = 0,
    val totalChallengesCompleted: Int = 0,
    val totalChallengesAbandoned: Int = 0,
    val totalDaysTracked: Int = 0,
    val overallSuccessRate: Float = 0f,
    val bestChallengeName: String = "",
    val bestChallengeRate: Float = 0f,
    val mostStruggledName: String = "",
    val mostStruggledRate: Float = 0f,
    val longestStreakEver: Int = 0,
    val completionRate: Float = 0f
)
