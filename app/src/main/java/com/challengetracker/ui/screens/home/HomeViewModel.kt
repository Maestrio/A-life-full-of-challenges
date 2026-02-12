package com.challengetracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.domain.model.ChallengeWithStats
import com.challengetracker.domain.usecase.GetActiveChallengesWithStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository,
    getActiveChallengesWithStats: GetActiveChallengesWithStatsUseCase
) : ViewModel() {

    val activeChallenges: StateFlow<List<ChallengeWithStats>> =
        getActiveChallengesWithStats()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingChallenges: StateFlow<List<ChallengeEntity>> =
        challengeRepository.getUpcomingChallenges()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChallengeCount: StateFlow<Int> =
        challengeRepository.getActiveChallengeCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _showCheckInDialog = MutableStateFlow<ChallengeWithStats?>(null)
    val showCheckInDialog: StateFlow<ChallengeWithStats?> = _showCheckInDialog.asStateFlow()

    init {
        viewModelScope.launch {
            challengeRepository.updateChallengeStatuses()
        }
    }

    fun showCheckIn(challenge: ChallengeWithStats) {
        _showCheckInDialog.value = challenge
    }

    fun dismissCheckIn() {
        _showCheckInDialog.value = null
    }

    fun quickCheckIn(
        challengeId: String,
        completed: Boolean,
        skipped: Boolean = false,
        note: String = "",
        mood: Int? = null,
        difficulty: Int? = null
    ) {
        viewModelScope.launch {
            checkInRepository.checkIn(
                challengeId = challengeId,
                date = LocalDate.now(),
                completed = completed,
                skipped = skipped,
                note = note,
                mood = mood,
                difficulty = difficulty
            )
            _showCheckInDialog.value = null
        }
    }
}
