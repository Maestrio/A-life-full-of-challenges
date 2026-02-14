package com.challengetracker.ui.screens.create

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challengetracker.data.local.database.entities.ChallengeMode
import com.challengetracker.data.local.database.entities.ChallengeType
import com.challengetracker.util.DateUtils
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateChallengeScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateChallengeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isEditing) "Edit Challenge" else "New Challenge") },
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
            // Challenge Mode selector
            Text("Challenge Mode", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.challengeMode == ChallengeMode.MULTI_DAY,
                    onClick = { viewModel.updateChallengeMode(ChallengeMode.MULTI_DAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Multi-day")
                }
                SegmentedButton(
                    selected = state.challengeMode == ChallengeMode.SINGLE_DAY,
                    onClick = { viewModel.updateChallengeMode(ChallengeMode.SINGLE_DAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Single day")
                }
            }

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Challenge Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null && state.name.isBlank(),
                singleLine = true
            )

            // Challenge Type
            Text("Challenge Type", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.challengeType == ChallengeType.ADDITION,
                    onClick = { viewModel.updateChallengeType(ChallengeType.ADDITION) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Addition")
                }
                SegmentedButton(
                    selected = state.challengeType == ChallengeType.SUBTRACTION,
                    onClick = { viewModel.updateChallengeType(ChallengeType.SUBTRACTION) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Subtraction")
                }
            }
            Text(
                text = if (state.challengeType == ChallengeType.ADDITION)
                    "Do something daily (exercise, write, meditate)"
                else
                    "Avoid something (no social media, no alcohol)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Terminal Goal
            OutlinedTextField(
                value = state.terminalGoal,
                onValueChange = viewModel::updateTerminalGoal,
                label = { Text("Terminal Goal") },
                placeholder = { Text("What do you actually want from this?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Start Date / Challenge Date picker
            Text(
                text = if (state.challengeMode == ChallengeMode.SINGLE_DAY) "Challenge Date" else "Start Date",
                style = MaterialTheme.typography.labelLarge
            )
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(DateUtils.formatDate(state.startDate))
            }

            // Duration (only for multi-day)
            if (state.challengeMode == ChallengeMode.MULTI_DAY) {
                Text("Duration", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7, 14, 21, 30, 60, 90).forEach { days ->
                        FilterChip(
                            selected = state.duration == days && !state.customDuration,
                            onClick = { viewModel.updateDuration(days) },
                            label = { Text("$days days") }
                        )
                    }
                    FilterChip(
                        selected = state.customDuration,
                        onClick = { viewModel.updateCustomDuration(state.duration) },
                        label = { Text("Custom") }
                    )
                }
                if (state.customDuration) {
                    OutlinedTextField(
                        value = state.duration.toString(),
                        onValueChange = { it.toIntOrNull()?.let { d -> viewModel.updateCustomDuration(d) } },
                        label = { Text("Custom days") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Success Threshold
                Text(
                    "Success Threshold: ${state.successThreshold}%",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = state.successThreshold.toFloat(),
                    onValueChange = { viewModel.updateThreshold(it.toInt()) },
                    valueRange = 50f..100f,
                    steps = 49
                )

                // Post Challenge Rule
                OutlinedTextField(
                    value = state.postChallengeRule,
                    onValueChange = viewModel::updatePostChallengeRule,
                    label = { Text("Post-Challenge Rule") },
                    placeholder = { Text("What happens after Day ${state.duration}?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            // Pre-Mortem
            OutlinedTextField(
                value = state.preMortem,
                onValueChange = viewModel::updatePreMortem,
                label = { Text("Pre-Mortem (optional)") },
                placeholder = { Text("How might you fail or miss the point?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Error
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                Text(if (state.isEditing) "Update Challenge" else "Create Challenge")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date picker dialog — no restrictions on date selection
    if (showDatePicker) {
        val initialMillis = state.startDate
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.updateStartDate(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
