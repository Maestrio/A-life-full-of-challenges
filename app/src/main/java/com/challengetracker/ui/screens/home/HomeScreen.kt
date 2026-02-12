package com.challengetracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challengetracker.ui.components.ChallengeCard
import com.challengetracker.ui.components.CheckInDialog
import com.challengetracker.ui.theme.WarningYellow
import com.challengetracker.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val activeChallenges by viewModel.activeChallenges.collectAsStateWithLifecycle()
    val upcomingChallenges by viewModel.upcomingChallenges.collectAsStateWithLifecycle()
    val activeCount by viewModel.activeChallengeCount.collectAsStateWithLifecycle()
    val checkInDialogChallenge by viewModel.showCheckInDialog.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Challenge Tracker") },
                actions = {
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.BarChart, "Statistics")
                    }
                    IconButton(onClick = onNavigateToArchive) {
                        Icon(Icons.Default.Archive, "Archive")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Create Challenge")
            }
        }
    ) { padding ->
        if (activeChallenges.isEmpty() && upcomingChallenges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No active challenges",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to create your first challenge",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Warning banner
                if (activeCount > 5) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarningYellow.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "You have $activeCount active challenges. Consider focusing on fewer to improve your success rate.",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Active challenges
                if (activeChallenges.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Challenges",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(activeChallenges, key = { it.challenge.id }) { challengeWithStats ->
                        ChallengeCard(
                            challengeWithStats = challengeWithStats,
                            onClick = { onNavigateToDetail(challengeWithStats.challenge.id) },
                            onQuickCheckIn = { viewModel.showCheckIn(challengeWithStats) }
                        )
                    }
                }

                // Upcoming challenges
                if (upcomingChallenges.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upcoming",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(upcomingChallenges, key = { it.id }) { challenge ->
                        Card(
                            onClick = { onNavigateToDetail(challenge.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = challenge.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Starts ${DateUtils.formatDate(challenge.startDate)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Check-in dialog
    checkInDialogChallenge?.let { challengeWithStats ->
        CheckInDialog(
            challengeName = challengeWithStats.challenge.name,
            onDismiss = { viewModel.dismissCheckIn() },
            onCheckIn = { completed, skipped, note, mood, difficulty ->
                viewModel.quickCheckIn(
                    challengeId = challengeWithStats.challenge.id,
                    completed = completed,
                    skipped = skipped,
                    note = note,
                    mood = mood,
                    difficulty = difficulty
                )
            }
        )
    }
}
