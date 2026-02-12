package com.challengetracker.data.local.database

import androidx.room.TypeConverter
import com.challengetracker.data.local.database.entities.ChallengeStatus
import com.challengetracker.data.local.database.entities.ChallengeType
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromChallengeType(value: ChallengeType): String = value.name

    @TypeConverter
    fun toChallengeType(value: String): ChallengeType = ChallengeType.valueOf(value)

    @TypeConverter
    fun fromChallengeStatus(value: ChallengeStatus): String = value.name

    @TypeConverter
    fun toChallengeStatus(value: String): ChallengeStatus = ChallengeStatus.valueOf(value)
}
