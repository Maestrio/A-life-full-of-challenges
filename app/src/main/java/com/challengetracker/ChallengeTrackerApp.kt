package com.challengetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ChallengeTrackerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val dailyChannel = NotificationChannel(
            "daily_reminder",
            "Daily Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily check-in reminders"
        }

        val weeklyChannel = NotificationChannel(
            "weekly_review",
            "Weekly Reviews",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Weekly review reminders"
        }

        val milestoneChannel = NotificationChannel(
            "milestones",
            "Milestones",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Challenge milestones and streaks"
        }

        manager.createNotificationChannel(dailyChannel)
        manager.createNotificationChannel(weeklyChannel)
        manager.createNotificationChannel(milestoneChannel)
    }
}
