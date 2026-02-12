package com.challengetracker.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengetracker.domain.model.InsightStats
import com.challengetracker.domain.usecase.GetInsightStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getInsightStats: GetInsightStatsUseCase
) : ViewModel() {

    private val _stats = MutableStateFlow(InsightStats())
    val stats: StateFlow<InsightStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            _stats.value = getInsightStats()
        }
    }
}
