package com.challengetracker.data.model

import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity

data class ExportData(
    val version: Int = 1,
    val exportedAt: String,
    val challenges: List<ChallengeEntity>,
    val checkIns: List<DailyCheckInEntity>,
    val reflections: List<WeeklyReflectionEntity>
)
