package com.challengetracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.data.local.datastore.SettingsDataStore
import com.challengetracker.domain.usecase.ExportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val exportDataUseCase: ExportDataUseCase
) : ViewModel() {

    val defaultThreshold = settingsDataStore.defaultThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 90)

    val defaultDuration = settingsDataStore.defaultDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    val dailyReminderEnabled = settingsDataStore.dailyReminderEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val dailyReminderHour = settingsDataStore.dailyReminderHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val dailyReminderMinute = settingsDataStore.dailyReminderMinute
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyReviewEnabled = settingsDataStore.weeklyReviewEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val weeklyReviewDay = settingsDataStore.weeklyReviewDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7)

    val darkMode = settingsDataStore.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _exportResult = MutableStateFlow<String?>(null)
    val exportResult: StateFlow<String?> = _exportResult.asStateFlow()

    fun setDefaultThreshold(value: Int) {
        viewModelScope.launch { settingsDataStore.setDefaultThreshold(value) }
    }

    fun setDefaultDuration(value: Int) {
        viewModelScope.launch { settingsDataStore.setDefaultDuration(value) }
    }

    fun setDailyReminderEnabled(value: Boolean) {
        viewModelScope.launch { settingsDataStore.setDailyReminderEnabled(value) }
    }

    fun setDailyReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch { settingsDataStore.setDailyReminderTime(hour, minute) }
    }

    fun setWeeklyReviewEnabled(value: Boolean) {
        viewModelScope.launch { settingsDataStore.setWeeklyReviewEnabled(value) }
    }

    fun setWeeklyReviewDay(value: Int) {
        viewModelScope.launch { settingsDataStore.setWeeklyReviewDay(value) }
    }

    fun setDarkMode(value: Boolean) {
        viewModelScope.launch { settingsDataStore.setDarkMode(value) }
    }

    fun exportData() {
        viewModelScope.launch {
            _exportResult.value = exportDataUseCase()
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }
}
