package com.challengetracker.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challengetracker.data.local.database.entities.ChallengeType
import com.challengetracker.ui.components.ChallengeCalendarView
import com.challengetracker.ui.components.StatsCard
import com.challengetracker.ui.theme.FailureRed
import com.challengetracker.ui.theme.SuccessGreen
import com.challengetracker.ui.theme.WarningYellow
import com.challengetracker.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToReview: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val challengeWithStats by viewModel.challengeWithStats.collectAsStateWithLifecycle()
    val failedCheckIns by viewModel.failedCheckIns.collectAsStateWithLifecycle()
    val reflections by viewModel.reflections.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedCheckIn by viewModel.selectedCheckIn.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    val stats = challengeWithStats ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stats.challenge.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(stats.challenge.id) }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { showArchiveDialog = true }) {
                        Icon(Icons.Default.Archive, "Archive")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete")
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
            // Header info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(if (stats.challenge.challengeType == ChallengeType.ADDITION) "Addition" else "Subtraction") }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(stats.challenge.status.name) }
                        )
                    }
                    if (stats.challenge.terminalGoal.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Terminal Goal",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stats.challenge.terminalGoal,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Stats overview
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    label = "Completed",
                    value = "${stats.completedDays}/${stats.daysElapsed}",
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Success Rate",
                    value = "${String.format("%.0f", stats.successRate)}%",
                    modifier = Modifier.weight(1f),
                    valueColor = when {
                        stats.successRate >= stats.challenge.successThreshold -> SuccessGreen
                        stats.successRate >= stats.challenge.successThreshold - 5 -> WarningYellow
                        else -> FailureRed
                    }
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(
                    label = "Current Streak",
                    value = "${stats.currentStreak}",
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Longest Streak",
                    value = "${stats.longestStreak}",
                    modifier = Modifier.weight(1f)
                )
            }

            // Calendar
            Text("Calendar", style = MaterialTheme.typography.titleMedium)
            ChallengeCalendarView(
                startDate = stats.challenge.startDate,
                endDate = stats.challenge.endDate,
                checkIns = stats.checkIns,
                onDayClick = { viewModel.selectDate(it) }
            )

            // Day of week breakdown
            val dowStats = viewModel.getDayOfWeekStats()
            if (dowStats.any { it.totalCount > 0 }) {
                Text("Day of Week Breakdown", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        dowStats.forEach { day ->
                            if (day.totalCount > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        DateUtils.getDayOfWeekName(day.dayOfWeek),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "${day.completedCount}/${day.totalCount} (${String.format("%.0f", day.successRate)}%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (day.successRate >= stats.challenge.successThreshold) SuccessGreen else FailureRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Slip Analysis
            if (failedCheckIns.isNotEmpty()) {
                Text("Slip Analysis", style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = FailureRed.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val patterns = viewModel.detectPatterns()
                        patterns.forEach { pattern ->
                            Text(
                                text = pattern,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = FailureRed,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        failedCheckIns.take(5).forEach { checkIn ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    DateUtils.formatShortDate(checkIn.date),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (checkIn.note.isNotBlank()) {
                                    Text(
                                        checkIn.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Weekly Reflections
            Text("Weekly Reflections", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = { onNavigateToReview(stats.challenge.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Write Weekly Reflection")
            }
            reflections.forEach { reflection ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Week ${reflection.weekNumber}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (reflection.reflection.isNotBlank()) {
                            Text(reflection.reflection, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (reflection.whatWorked.isNotBlank()) {
                            Text(
                                "What worked: ${reflection.whatWorked}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessGreen
                            )
                        }
                        if (reflection.whatDidnt.isNotBlank()) {
                            Text(
                                "What didn't: ${reflection.whatDidnt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FailureRed
                            )
                        }
                    }
                }
            }

            // Post-challenge rule
            if (stats.challenge.postChallengeRule.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Post-Challenge Plan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(stats.challenge.postChallengeRule, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date selection dialog for check-in or edit
    selectedDate?.let { date ->
        val existingCheckIn = selectedCheckIn
        if (existingCheckIn != null) {
            EditCheckInDialog(
                date = date,
                checkIn = existingCheckIn,
                onDismiss = { viewModel.dismissDateSelection() },
                onSave = { completed, note, mood, difficulty, editReason ->
                    viewModel.editCheckIn(existingCheckIn, completed, note, mood, difficulty, editReason)
                }
            )
        } else {
            CheckInForDateDialog(
                date = date,
                onDismiss = { viewModel.dismissDateSelection() },
                onCheckIn = { completed, skipped, note, mood, difficulty ->
                    viewModel.checkIn(date, completed, skipped, note, mood, difficulty)
                }
            )
        }
    }

    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Challenge?") },
            text = { Text("This will permanently delete the challenge and all its data. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteChallenge()
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FailureRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Archive confirmation
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Archive Challenge?") },
            text = { Text("This will end the challenge early and move it to your archive.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.archiveChallenge()
                    showArchiveDialog = false
                    onNavigateBack()
                }) { Text("Archive") }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CheckInForDateDialog(
    date: java.time.LocalDate,
    onDismiss: () -> Unit,
    onCheckIn: (completed: Boolean, skipped: Boolean, note: String, mood: Int?, difficulty: Int?) -> Unit
) {
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Check in for ${DateUtils.formatDate(date)}") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onCheckIn(true, false, note, null, null) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) { Text("Yes") }
                    Button(
                        onClick = { onCheckIn(false, false, note, null, null) },
                        colors = ButtonDefaults.buttonColors(containerColor = FailureRed)
                    ) { Text("No") }
                    OutlinedButton(onClick = { onCheckIn(false, true, note, null, null) }) { Text("Skip") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditCheckInDialog(
    date: java.time.LocalDate,
    checkIn: com.challengetracker.data.local.database.entities.DailyCheckInEntity,
    onDismiss: () -> Unit,
    onSave: (completed: Boolean, note: String, mood: Int?, difficulty: Int?, editReason: String) -> Unit
) {
    var completed by remember { mutableStateOf(checkIn.completed) }
    var note by remember { mutableStateOf(checkIn.note) }
    var editReason by remember { mutableStateOf("") }
    var mood by remember { mutableFloatStateOf((checkIn.mood ?: 3).toFloat()) }
    var difficulty by remember { mutableFloatStateOf((checkIn.difficulty ?: 3).toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${DateUtils.formatDate(date)}") },
        text = {
            Column {
                Text(
                    "Editing past data affects your statistics",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningYellow
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = completed, onClick = { completed = true }, label = { Text("Completed") })
                    FilterChip(selected = !completed, onClick = { completed = false }, label = { Text("Missed") })
                }
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = editReason, onValueChange = { editReason = it }, label = { Text("Reason for edit") }, modifier = Modifier.fillMaxWidth())
                if (checkIn.wasEdited) {
                    Text("Previously edited", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(completed, note, mood.toInt(), difficulty.toInt(), editReason) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
