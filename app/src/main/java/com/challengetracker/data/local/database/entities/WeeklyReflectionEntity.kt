package com.challengetracker.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "weekly_reflections",
    foreignKeys = [
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["challengeId"]),
        Index(value = ["challengeId", "weekNumber"], unique = true)
    ]
)
data class WeeklyReflectionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val challengeId: String,
    val weekNumber: Int,
    val reflection: String = "",
    val isOnTrack: Boolean = true,
    val whatWorked: String = "",
    val whatDidnt: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)
