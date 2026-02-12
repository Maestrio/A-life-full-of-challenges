package com.challengetracker.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challengetracker.ui.components.StatsCard
import com.challengetracker.ui.theme.FailureRed
import com.challengetracker.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Overall Dashboard", style = MaterialTheme.typography.headlineMedium)

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    label = "Challenges Created",
                    value = "${stats.totalChallengesCreated}",
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Completed",
                    value = "${stats.totalChallengesCompleted}",
                    modifier = Modifier.weight(1f),
                    valueColor = SuccessGreen
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    label = "Days Tracked",
                    value = "${stats.totalDaysTracked}",
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Overall Success",
                    value = "${String.format("%.0f", stats.overallSuccessRate)}%",
                    modifier = Modifier.weight(1f),
                    valueColor = if (stats.overallSuccessRate >= 80) SuccessGreen else FailureRed
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    label = "Longest Streak",
                    value = "${stats.longestStreakEver}",
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Completion Rate",
                    value = "${String.format("%.0f", stats.completionRate)}%",
                    modifier = Modifier.weight(1f)
                )
            }

            if (stats.bestChallengeName.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Best Performing", style = MaterialTheme.typography.labelLarge)
                        Text(
                            stats.bestChallengeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text("${String.format("%.0f", stats.bestChallengeRate)}% success rate")
                    }
                }
            }

            if (stats.mostStruggledName.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Most Struggled", style = MaterialTheme.typography.labelLarge)
                        Text(
                            stats.mostStruggledName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FailureRed
                        )
                        Text("${String.format("%.0f", stats.mostStruggledRate)}% success rate")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
