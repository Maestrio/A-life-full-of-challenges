package com.challengetracker.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.domain.model.ChallengeWithStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    val archivedChallenges: StateFlow<List<ChallengeWithStats>> =
        challengeRepository.getCompletedAndArchived().flatMapLatest { challenges ->
            if (challenges.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = challenges.map { checkInRepository.getCheckInsForChallenge(it.id) }
                combine(flows) { checkInsArray ->
                    challenges.mapIndexed { index, challenge ->
                        ChallengeWithStats.calculate(challenge, checkInsArray[index].toList())
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
