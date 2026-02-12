package com.challengetracker.domain.usecase

import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.domain.model.ChallengeWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetChallengeWithStatsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository
) {
    operator fun invoke(challengeId: String): Flow<ChallengeWithStats?> {
        return combine(
            challengeRepository.getChallengeById(challengeId),
            checkInRepository.getCheckInsForChallenge(challengeId)
        ) { challenge, checkIns ->
            challenge?.let { ChallengeWithStats.calculate(it, checkIns) }
        }
    }
}
