package com.challengetracker.ui.screens.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity
import com.challengetracker.data.repository.ChallengeRepository
import com.challengetracker.data.repository.WeeklyReflectionRepository
import com.challengetracker.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ReviewState(
    val challengeName: String = "",
    val weekNumber: Int = 1,
    val reflection: String = "",
    val isOnTrack: Boolean = true,
    val whatWorked: String = "",
    val whatDidnt: String = "",
    val saved: Boolean = false
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val challengeRepository: ChallengeRepository,
    private val reflectionRepository: WeeklyReflectionRepository
) : ViewModel() {

    private val challengeId: String = checkNotNull(savedStateHandle["challengeId"])

    private val _state = MutableStateFlow(ReviewState())
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val challenge = challengeRepository.getChallengeByIdOnce(challengeId)
            if (challenge != null) {
                val weekNumber = DateUtils.getWeekNumber(challenge.startDate, LocalDate.now())
                _state.value = _state.value.copy(
                    challengeName = challenge.name,
                    weekNumber = weekNumber
                )
            }
        }
    }

    fun updateReflection(text: String) { _state.value = _state.value.copy(reflection = text) }
    fun updateIsOnTrack(value: Boolean) { _state.value = _state.value.copy(isOnTrack = value) }
    fun updateWhatWorked(text: String) { _state.value = _state.value.copy(whatWorked = text) }
    fun updateWhatDidnt(text: String) { _state.value = _state.value.copy(whatDidnt = text) }

    fun save() {
        viewModelScope.launch {
            val current = _state.value
            reflectionRepository.saveReflection(
                WeeklyReflectionEntity(
                    challengeId = challengeId,
                    weekNumber = current.weekNumber,
                    reflection = current.reflection,
                    isOnTrack = current.isOnTrack,
                    whatWorked = current.whatWorked,
                    whatDidnt = current.whatDidnt
                )
            )
            _state.value = current.copy(saved = true)
        }
    }
}
