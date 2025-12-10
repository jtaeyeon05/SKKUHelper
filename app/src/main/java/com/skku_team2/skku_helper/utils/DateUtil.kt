package com.skku_team2.skku_helper.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs


object DateUtil {
    private val canvasDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    enum class TimeType {
        UPCOMING, OVERDUE, NO_DATA, ERROR
    }

    data class DateResult(
        val remainingSeconds: Long?,
        val type: TimeType
    )

    fun parseTime(time: String): OffsetDateTime {
        return OffsetDateTime.parse(time, canvasDateTimeFormatter)
    }

    fun calculateRemainingTime(dueAt: String?): DateResult {
        if (dueAt == null) return DateResult(null, TimeType.NO_DATA)

        return try {
            val now = OffsetDateTime.now()
            val dueDate = OffsetDateTime.parse(dueAt, canvasDateTimeFormatter)

            val diffInSeconds = ChronoUnit.SECONDS.between(now, dueDate)
            val type = if (diffInSeconds >= 0) TimeType.UPCOMING else TimeType.OVERDUE

            DateResult(diffInSeconds, type)
        } catch (e: Exception) {
            e.printStackTrace()
            DateResult(null, TimeType.ERROR)
        }
    }

    fun formatRemainingTime(seconds: Long?): String {
        if (seconds == null) return ""
        val absSeconds = abs(seconds)

        val days = absSeconds / (24 * 3600)
        val hours = (absSeconds % (24 * 3600)) / 3600
        val minutes = (absSeconds % 3600) / 60

        return when {
            days > 0 -> "$days Days"
            hours > 0 -> "$hours Hours"
            minutes > 0 -> "$minutes Minutes"
            else -> ""
        }
    }
}