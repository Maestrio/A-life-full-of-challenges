package com.challengetracker.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "daily_checkins",
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
        Index(value = ["challengeId", "date"], unique = true)
    ]
)
data class DailyCheckInEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val challengeId: String,
    val date: LocalDate,
    val completed: Boolean,
    val skipped: Boolean = false,
    val note: String = "",
    val mood: Int? = null,
    val difficulty: Int? = null,
    val checkinTime: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
    val wasEdited: Boolean = false,
    val editReason: String = ""
)
