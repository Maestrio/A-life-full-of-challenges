package com.challengetracker.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.CheckInRepository
import com.challengetracker.data.repository.WeeklyReflectionRepository
import com.challengetracker.domain.model.ChallengeWithStats
import com.challengetracker.domain.model.DayOfWeekStats
import com.challengetracker.domain.usecase.GetChallengeWithStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getChallengeWithStats: GetChallengeWithStatsUseCase,
    private val challengeRepository: ChallengeRepository,
    private val checkInRepository: CheckInRepository,
    private val reflectionRepository: WeeklyReflectionRepository
) : ViewModel() {

    private val challengeId: String = checkNotNull(savedStateHandle["challengeId"])

    val challengeWithStats: StateFlow<ChallengeWithStats?> =
        getChallengeWithStats(challengeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val failedCheckIns: StateFlow<List<DailyCheckInEntity>> =
        checkInRepository.getFailedCheckIns(challengeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reflections: StateFlow<List<WeeklyReflectionEntity>> =
        reflectionRepository.getReflectionsForChallenge(challengeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedCheckIn = MutableStateFlow<DailyCheckInEntity?>(null)
    val selectedCheckIn: StateFlow<DailyCheckInEntity?> = _selectedCheckIn.asStateFlow()

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            val checkIns = checkInRepository.getCheckInsForChallengeOnce(challengeId)
            _selectedCheckIn.value = checkIns.find { it.date == date }
        }
    }

    fun dismissDateSelection() {
        _selectedDate.value = null
        _selectedCheckIn.value = null
    }

    fun checkIn(
        date: LocalDate,
        completed: Boolean,
        skipped: Boolean = false,
        note: String = "",
        mood: Int? = null,
        difficulty: Int? = null
    ) {
        viewModelScope.launch {
            checkInRepository.checkIn(challengeId, date, completed, skipped, note, mood, difficulty)
            _selectedDate.value = null
            _selectedCheckIn.value = null
        }
    }

    fun editCheckIn(
        checkIn: DailyCheckInEntity,
        completed: Boolean,
        note: String,
        mood: Int?,
        difficulty: Int?,
        editReason: String
    ) {
        viewModelScope.launch {
            checkInRepository.editCheckIn(checkIn, completed, note, mood, difficulty, editReason)
            _selectedDate.value = null
            _selectedCheckIn.value = null
        }
    }

    fun archiveChallenge() {
        viewModelScope.launch {
            challengeRepository.archiveChallenge(challengeId)
        }
    }

    fun deleteChallenge() {
        viewModelScope.launch {
            challengeRepository.deleteChallenge(challengeId)
        }
    }

    fun getDayOfWeekStats(): List<DayOfWeekStats> {
        val stats = challengeWithStats.value ?: return emptyList()
        return DayOfWeek.entries.map { dow ->
            val dayCheckIns = stats.checkIns.filter { it.date.dayOfWeek == dow && !it.skipped }
            val completed = dayCheckIns.count { it.completed }
            val total = dayCheckIns.size
            DayOfWeekStats(
                dayOfWeek = dow,
                completedCount = completed,
                totalCount = total,
                successRate = if (total > 0) completed.toFloat() / total * 100f else 0f
            )
        }
    }

    fun detectPatterns(): List<String> {
        val stats = challengeWithStats.value ?: return emptyList()
        val patterns = mutableListOf<String>()
        val failed = stats.checkIns.filter { !it.completed && !it.skipped }

        // Day of week pattern
        val dowCounts = failed.groupBy { it.date.dayOfWeek }
        val worstDay = dowCounts.maxByOrNull { it.value.size }
        if (worstDay != null && worstDay.value.size >= 3) {
            patterns.add("You've missed ${worstDay.value.size} ${worstDay.key.name.lowercase().replaceFirstChar { it.uppercase() }}s — consider why.")
        }

        return patterns
    }
}
