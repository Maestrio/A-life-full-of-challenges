package com.challengetracker.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(
    private val context: Context
) {
    companion object {
        val DEFAULT_THRESHOLD = intPreferencesKey("default_threshold")
        val DEFAULT_DURATION = intPreferencesKey("default_duration")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val WEEKLY_REVIEW_ENABLED = booleanPreferencesKey("weekly_review_enabled")
        val WEEKLY_REVIEW_DAY = intPreferencesKey("weekly_review_day")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
    }

    val defaultThreshold: Flow<Int> = context.dataStore.data.map { it[DEFAULT_THRESHOLD] ?: 90 }
    val defaultDuration: Flow<Int> = context.dataStore.data.map { it[DEFAULT_DURATION] ?: 30 }
    val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[DAILY_REMINDER_ENABLED] ?: true }
    val dailyReminderHour: Flow<Int> = context.dataStore.data.map { it[DAILY_REMINDER_HOUR] ?: 20 }
    val dailyReminderMinute: Flow<Int> = context.dataStore.data.map { it[DAILY_REMINDER_MINUTE] ?: 0 }
    val weeklyReviewEnabled: Flow<Boolean> = context.dataStore.data.map { it[WEEKLY_REVIEW_ENABLED] ?: true }
    val weeklyReviewDay: Flow<Int> = context.dataStore.data.map { it[WEEKLY_REVIEW_DAY] ?: 7 }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val themeColor: Flow<String> = context.dataStore.data.map { it[THEME_COLOR] ?: "purple" }
    val autoBackup: Flow<Boolean> = context.dataStore.data.map { it[AUTO_BACKUP] ?: false }

    suspend fun setDefaultThreshold(value: Int) {
        context.dataStore.edit { it[DEFAULT_THRESHOLD] = value }
    }

    suspend fun setDefaultDuration(value: Int) {
        context.dataStore.edit { it[DEFAULT_DURATION] = value }
    }

    suspend fun setDailyReminderEnabled(value: Boolean) {
        context.dataStore.edit { it[DAILY_REMINDER_ENABLED] = value }
    }

    suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[DAILY_REMINDER_HOUR] = hour
            it[DAILY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun setWeeklyReviewEnabled(value: Boolean) {
        context.dataStore.edit { it[WEEKLY_REVIEW_ENABLED] = value }
    }

    suspend fun setWeeklyReviewDay(value: Int) {
        context.dataStore.edit { it[WEEKLY_REVIEW_DAY] = value }
    }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = value }
    }

    suspend fun setThemeColor(value: String) {
        context.dataStore.edit { it[THEME_COLOR] = value }
    }

    suspend fun setAutoBackup(value: Boolean) {
        context.dataStore.edit { it[AUTO_BACKUP] = value }
    }
}
