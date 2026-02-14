package com.challengetracker.ui.screens.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.ChallengeMode
import com.challengetracker.data.local.database.entities.ChallengeStatus
import com.challengetracker.data.local.database.entities.ChallengeType
import com.challengetracker.data.local.datastore.SettingsDataStore
import com.challengetracker.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class CreateChallengeState(
    val name: String = "",
    val description: String = "",
    val terminalGoal: String = "",
    val challengeType: ChallengeType = ChallengeType.ADDITION,
    val challengeMode: ChallengeMode = ChallengeMode.MULTI_DAY,
    val duration: Int = 30,
    val customDuration: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val successThreshold: Int = 90,
    val postChallengeRule: String = "",
    val preMortem: String = "",
    val isEditing: Boolean = false,
    val editingId: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val settingsDataStore: SettingsDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val challengeId: String? = savedStateHandle["challengeId"]

    private val _state = MutableStateFlow(CreateChallengeState())
    val state: StateFlow<CreateChallengeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val defaultThreshold = settingsDataStore.defaultThreshold.first()
            val defaultDuration = settingsDataStore.defaultDuration.first()
            _state.value = _state.value.copy(
                successThreshold = defaultThreshold,
                duration = defaultDuration
            )

            if (challengeId != null) {
                val challenge = challengeRepository.getChallengeByIdOnce(challengeId)
                if (challenge != null) {
                    _state.value = _state.value.copy(
                        name = challenge.name,
                        description = challenge.description,
                        terminalGoal = challenge.terminalGoal,
                        challengeType = challenge.challengeType,
                        challengeMode = challenge.challengeMode,
                        duration = challenge.duration,
                        startDate = challenge.startDate,
                        successThreshold = challenge.successThreshold,
                        postChallengeRule = challenge.postChallengeRule,
                        preMortem = challenge.preMortem,
                        isEditing = true,
                        editingId = challenge.id
                    )
                }
            }
        }
    }

    fun updateName(name: String) { _state.value = _state.value.copy(name = name, error = null) }
    fun updateDescription(desc: String) { _state.value = _state.value.copy(description = desc) }
    fun updateTerminalGoal(goal: String) { _state.value = _state.value.copy(terminalGoal = goal) }
    fun updateChallengeType(type: ChallengeType) { _state.value = _state.value.copy(challengeType = type) }
    fun updateChallengeMode(mode: ChallengeMode) { _state.value = _state.value.copy(challengeMode = mode) }
    fun updateDuration(duration: Int) { _state.value = _state.value.copy(duration = duration, customDuration = false) }
    fun updateCustomDuration(duration: Int) { _state.value = _state.value.copy(duration = duration, customDuration = true) }
    fun updateStartDate(date: LocalDate) { _state.value = _state.value.copy(startDate = date) }
    fun updateThreshold(threshold: Int) { _state.value = _state.value.copy(successThreshold = threshold) }
    fun updatePostChallengeRule(rule: String) { _state.value = _state.value.copy(postChallengeRule = rule) }
    fun updatePreMortem(preMortem: String) { _state.value = _state.value.copy(preMortem = preMortem) }

    fun save() {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.value = current.copy(error = "Challenge name is required")
            return
        }

        val isSingleDay = current.challengeMode == ChallengeMode.SINGLE_DAY

        if (!isSingleDay && current.duration < 1) {
            _state.value = current.copy(error = "Duration must be at least 1 day")
            return
        }
        if (!isSingleDay && current.successThreshold !in 50..100) {
            _state.value = current.copy(error = "Threshold must be between 50% and 100%")
            return
        }

        _state.value = current.copy(isSaving = true)

        viewModelScope.launch {
            val duration = if (isSingleDay) 1 else current.duration
            val endDate = current.startDate.plusDays(duration.toLong() - 1)
            val threshold = if (isSingleDay) 100 else current.successThreshold
            val today = LocalDate.now()
            val status = when {
                current.startDate.isAfter(today) -> ChallengeStatus.UPCOMING
                endDate.isBefore(today) -> ChallengeStatus.COMPLETED
                else -> ChallengeStatus.ACTIVE
            }

            val challenge = ChallengeEntity(
                id = current.editingId ?: UUID.randomUUID().toString(),
                name = current.name,
                description = current.description,
                terminalGoal = current.terminalGoal,
                challengeType = current.challengeType,
                challengeMode = current.challengeMode,
                duration = duration,
                startDate = current.startDate,
                endDate = endDate,
                successThreshold = threshold,
                postChallengeRule = current.postChallengeRule,
                preMortem = current.preMortem,
                status = status
            )

            if (current.isEditing) {
                challengeRepository.updateChallenge(challenge)
            } else {
                challengeRepository.createChallenge(challenge)
            }

            _state.value = _state.value.copy(isSaving = false, saved = true)
        }
    }
}
