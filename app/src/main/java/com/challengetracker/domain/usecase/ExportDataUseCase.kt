package com.challengetracker.domain.usecase

import com.challengetracker.data.model.ExportData
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.data.repository.WeeklyReflectionRepository
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository,
    private val reflectionRepository: WeeklyReflectionRepository
) {
    suspend operator fun invoke(): String {
        val exportData = ExportData(
            exportedAt = LocalDateTime.now().toString(),
            challenges = challengeRepository.getAllChallengesOnce(),
            checkIns = checkInRepository.getAllCheckInsOnce(),
            reflections = reflectionRepository.getAllReflectionsOnce()
        )
        return GsonBuilder().setPrettyPrinting().create().toJson(exportData)
    }
}
