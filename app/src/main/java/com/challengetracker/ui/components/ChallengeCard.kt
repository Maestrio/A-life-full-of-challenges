package com.challengetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.challengetracker.data.local.database.entities.ChallengeType
import com.challengetracker.domain.model.ChallengeWithStats
import com.challengetracker.ui.theme.FailureRed
import com.challengetracker.ui.theme.SkippedOrange
import com.challengetracker.ui.theme.SuccessGreen
import com.challengetracker.ui.theme.WarningYellow

@Composable
fun ChallengeCard(
    challengeWithStats: ChallengeWithStats,
    onClick: () -> Unit,
    onQuickCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val challenge = challengeWithStats.challenge
    val progressColor by animateColorAsState(
        targetValue = when {
            challengeWithStats.successRate >= challenge.successThreshold -> SuccessGreen
            challengeWithStats.successRate >= challenge.successThreshold - 5 -> WarningYellow
            else -> FailureRed
        },
        label = "progressColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (challenge.challengeType == ChallengeType.ADDITION) Icons.Default.Add else Icons.Default.Remove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = challenge.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TodayStatusIndicator(
                    checkedIn = challengeWithStats.todayCheckedIn,
                    skipped = challengeWithStats.todaySkipped,
                    onQuickCheckIn = onQuickCheckIn
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Day ${challengeWithStats.daysElapsed} of ${challenge.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%.0f", challengeWithStats.successRate)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { challengeWithStats.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = if (challengeWithStats.currentStreak > 0) SkippedOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${challengeWithStats.currentStreak} day streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${challengeWithStats.daysRemaining} days left",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TodayStatusIndicator(
    checkedIn: Boolean,
    skipped: Boolean,
    onQuickCheckIn: () -> Unit
) {
    val icon = when {
        checkedIn -> Icons.Default.Check
        skipped -> Icons.Default.SkipNext
        else -> Icons.Default.Close
    }
    val tint = when {
        checkedIn -> SuccessGreen
        skipped -> SkippedOrange
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    androidx.compose.material3.IconButton(
        onClick = onQuickCheckIn,
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Today's status",
            tint = tint
        )
    }
}
