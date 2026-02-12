package com.challengetracker.domain.usecase

import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.domain.model.ChallengeWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetActiveChallengesWithStatsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository
) {
    operator fun invoke(): Flow<List<ChallengeWithStats>> {
        return challengeRepository.getActiveChallenges().flatMapLatest { challenges ->
            if (challenges.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = challenges.map { challenge ->
                    checkInRepository.getCheckInsForChallenge(challenge.id)
                }
                combine(flows) { checkInsArray ->
                    challenges.mapIndexed { index, challenge ->
                        ChallengeWithStats.calculate(challenge, checkInsArray[index].toList())
                    }
                }
            }
        }
    }
}
