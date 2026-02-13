package com.challengetracker.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.challengetracker.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "daily_reminder")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to check in!")
            .setContentText("How did your challenges go today?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    fun showWeeklyReviewReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "weekly_review")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Weekly Review")
            .setContentText("How did your week go? Take a moment to reflect.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1002, notification)
    }

    fun showMilestone(challengeName: String, milestone: String) {
        val notification = NotificationCompat.Builder(context, "milestones")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Milestone reached!")
            .setContentText("$challengeName: $milestone")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(challengeName.hashCode(), notification)
    }

    fun showChallengeStarting(challengeName: String) {
        val notification = NotificationCompat.Builder(context, "milestones")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Challenge starting tomorrow!")
            .setContentText("$challengeName begins tomorrow. Are you ready?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(challengeName.hashCode() + 100, notification)
    }

    fun showChallengeEnding(challengeName: String, percentage: String) {
        val notification = NotificationCompat.Builder(context, "milestones")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Last day of $challengeName!")
            .setContentText("You're at $percentage%. Finish strong!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(challengeName.hashCode() + 200, notification)
    }
}
