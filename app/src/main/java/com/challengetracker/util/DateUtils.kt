package com.challengetracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d")

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)
    fun formatShortDate(date: LocalDate): String = date.format(shortDateFormatter)

    fun daysBetween(start: LocalDate, end: LocalDate): Long = ChronoUnit.DAYS.between(start, end)

    fun getWeekNumber(startDate: LocalDate, currentDate: LocalDate): Int {
        val daysBetween = ChronoUnit.DAYS.between(startDate, currentDate).toInt()
        return (daysBetween / 7) + 1
    }

    fun getDayOfWeekName(dayOfWeek: DayOfWeek): String {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
}
