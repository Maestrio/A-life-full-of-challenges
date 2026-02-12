package com.challengetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.challengetracker.ui.theme.FailureRed
import com.challengetracker.ui.theme.SkippedOrange
import com.challengetracker.ui.theme.SuccessGreen

@Composable
fun CheckInDialog(
    challengeName: String,
    onDismiss: () -> Unit,
    onCheckIn: (completed: Boolean, skipped: Boolean, note: String, mood: Int?, difficulty: Int?) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var showDetails by remember { mutableStateOf(false) }
    var mood by remember { mutableFloatStateOf(3f) }
    var difficulty by remember { mutableFloatStateOf(3f) }
    var trackMood by remember { mutableStateOf(false) }
    var trackDifficulty by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = challengeName) },
        text = {
            Column {
                Text("How did today go?", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            onCheckIn(true, false, note, if (trackMood) mood.toInt() else null, if (trackDifficulty) difficulty.toInt() else null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Yes")
                    }
                    Button(
                        onClick = {
                            showDetails = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FailureRed)
                    ) {
                        Text("No")
                    }
                    OutlinedButton(
                        onClick = {
                            onCheckIn(false, true, note, if (trackMood) mood.toInt() else null, if (trackDifficulty) difficulty.toInt() else null)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkippedOrange)
                    ) {
                        Text("Skip")
                    }
                }

                if (showDetails) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("What happened?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { trackMood = !trackMood }) {
                    Text(if (trackMood) "Hide mood" else "Track mood")
                }
                if (trackMood) {
                    Text("Mood: ${mood.toInt()}/5", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = mood,
                        onValueChange = { mood = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }

                TextButton(onClick = { trackDifficulty = !trackDifficulty }) {
                    Text(if (trackDifficulty) "Hide difficulty" else "Track difficulty")
                }
                if (trackDifficulty) {
                    Text("Difficulty: ${difficulty.toInt()}/5", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }
            }
        },
        confirmButton = {
            if (showDetails) {
                Button(
                    onClick = {
                        onCheckIn(false, false, note, if (trackMood) mood.toInt() else null, if (trackDifficulty) difficulty.toInt() else null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FailureRed)
                ) {
                    Text("Log as missed")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
