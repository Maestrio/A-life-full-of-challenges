package com.challengetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.challengetracker.data.local.database.entities.DailyCheckInEntity
import com.challengetracker.ui.theme.FailureRed
import com.challengetracker.ui.theme.FutureGray
import com.challengetracker.ui.theme.SkippedOrange
import com.challengetracker.ui.theme.SuccessGreen
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ChallengeCalendarView(
    startDate: LocalDate,
    endDate: LocalDate,
    checkIns: List<DailyCheckInEntity>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(LocalDate.now())) }
    val checkInMap = remember(checkIns) { checkIns.associateBy { it.date } }
    val today = LocalDate.now()

    Column(modifier = modifier) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous month")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next month")
            }
        }

        // Day of week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val startDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=Monday

        val totalDays = currentMonth.lengthOfMonth()
        val rows = (totalDays + startDayOfWeek - 1 + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 1..7) {
                    val dayIndex = row * 7 + col - startDayOfWeek + 1
                    if (dayIndex in 1..totalDays) {
                        val date = currentMonth.atDay(dayIndex)
                        val checkIn = checkInMap[date]
                        val isInRange = !date.isBefore(startDate) && !date.isAfter(endDate)
                        val isFuture = date.isAfter(today)

                        val bgColor = when {
                            !isInRange -> Color.Transparent
                            isFuture -> FutureGray.copy(alpha = 0.3f)
                            checkIn == null -> Color.Transparent
                            checkIn.skipped -> SkippedOrange
                            checkIn.completed -> SuccessGreen
                            else -> FailureRed
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable(enabled = isInRange && !isFuture) { onDayClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayIndex.toString(),
                                fontSize = 12.sp,
                                color = when {
                                    !isInRange -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    checkIn != null && !isFuture -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}
