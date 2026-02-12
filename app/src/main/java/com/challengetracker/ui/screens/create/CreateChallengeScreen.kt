package com.challengetracker.ui.screens.create

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challengetracker.data.local.database.entities.ChallengeType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateChallengeScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateChallengeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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

            // Duration
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
}
