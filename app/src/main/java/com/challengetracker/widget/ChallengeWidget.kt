package com.challengetracker.widget

import android.content.Context
import android.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.room.Room
import com.challengetracker.data.local.database.AppDatabase
import com.challengetracker.data.local.database.entities.ChallengeStatus
import com.challengetracker.domain.model.ChallengeWithStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChallengeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val challenges = withContext(Dispatchers.IO) {
            loadChallenges(context)
        }

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color.WHITE)
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Challenges",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))

                    if (challenges.isEmpty()) {
                        Text(
                            text = "No active challenges",
                            style = TextStyle(fontSize = 14.sp)
                        )
                    } else {
                        challenges.take(4).forEach { stats ->
                            Row(
                                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (stats.todayCheckedIn) "✓" else "○",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = ColorProvider(
                                            if (stats.todayCheckedIn) Color.parseColor("#4CAF50")
                                            else Color.GRAY
                                        )
                                    )
                                )
                                Spacer(modifier = GlanceModifier.width(8.dp))
                                Column(modifier = GlanceModifier.defaultWeight()) {
                                    Text(
                                        text = stats.challenge.name,
                                        style = TextStyle(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Day ${stats.daysElapsed}/${stats.challenge.duration} · ${String.format("%.0f", stats.successRate)}%",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = ColorProvider(Color.GRAY)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadChallenges(context: Context): List<ChallengeWithStats> {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "challenge_tracker_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
        try {
            val challenges = db.challengeDao().getAllChallengesOnce()
                .filter { it.status == ChallengeStatus.ACTIVE }
            return challenges.map { challenge ->
                val checkIns = db.dailyCheckInDao().getCheckInsForChallengeOnce(challenge.id)
                ChallengeWithStats.calculate(challenge, checkIns)
            }.sortedBy { it.daysRemaining }
        } finally {
            db.close()
        }
    }
}

class ChallengeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ChallengeWidget()
}
