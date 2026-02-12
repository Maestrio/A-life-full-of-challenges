package com.challengetracker.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val threshold by viewModel.defaultThreshold.collectAsStateWithLifecycle()
    val duration by viewModel.defaultDuration.collectAsStateWithLifecycle()
    val dailyReminder by viewModel.dailyReminderEnabled.collectAsStateWithLifecycle()
    val reminderHour by viewModel.dailyReminderHour.collectAsStateWithLifecycle()
    val reminderMinute by viewModel.dailyReminderMinute.collectAsStateWithLifecycle()
    val weeklyReview by viewModel.weeklyReviewEnabled.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Defaults section
            Text("Defaults", style = MaterialTheme.typography.titleMedium)

            Text("Default success threshold: $threshold%", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = threshold.toFloat(),
                onValueChange = { viewModel.setDefaultThreshold(it.toInt()) },
                valueRange = 50f..100f,
                steps = 49
            )

            Text("Default duration: $duration days", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = duration.toFloat(),
                onValueChange = { viewModel.setDefaultDuration(it.toInt()) },
                valueRange = 7f..90f,
                steps = 82
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Notifications section
            Text("Notifications", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily reminder", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = dailyReminder, onCheckedChange = viewModel::setDailyReminderEnabled)
            }
            if (dailyReminder) {
                Text(
                    "Reminder at ${String.format("%02d:%02d", reminderHour, reminderMinute)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Weekly review reminder", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = weeklyReview, onCheckedChange = viewModel::setWeeklyReviewEnabled)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Appearance section
            Text("Appearance", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark mode", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = darkMode, onCheckedChange = viewModel::setDarkMode)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Data section
            Text("Data", style = MaterialTheme.typography.titleMedium)

            OutlinedButton(
                onClick = { viewModel.exportData() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Data (JSON)")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
