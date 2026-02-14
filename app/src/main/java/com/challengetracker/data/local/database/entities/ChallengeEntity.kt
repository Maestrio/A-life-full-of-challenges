package com.challengetracker.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class ChallengeType {
    ADDITION, SUBTRACTION
}

enum class ChallengeStatus {
    UPCOMING, ACTIVE, COMPLETED, ARCHIVED
}

enum class ChallengeMode {
    MULTI_DAY, SINGLE_DAY
}

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val terminalGoal: String = "",
    val challengeType: ChallengeType = ChallengeType.ADDITION,
    val challengeMode: ChallengeMode = ChallengeMode.MULTI_DAY,
    val duration: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val successThreshold: Int = 90,
    val postChallengeRule: String = "",
    val status: ChallengeStatus = ChallengeStatus.UPCOMING,
    val preMortem: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now()
)
