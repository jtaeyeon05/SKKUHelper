package com.skku_team2.skku_helper.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


object DateUtil {
    private val canvasDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    sealed interface RemainingTime {
        data class Days(val value: Long) : RemainingTime
        data class Hours(val value: Long) : RemainingTime
        data class Minutes(val value: Long) : RemainingTime
        data class Overdue(val value: Long) : RemainingTime
        data object Closed : RemainingTime
        data object NoData : RemainingTime
        data object Error : RemainingTime
    }

    fun calculateRemainingTime(dueAt: String?): RemainingTime {
        if (dueAt == null) return RemainingTime.NoData
        return try {
            val now = OffsetDateTime.now()
            val dueDate = OffsetDateTime.parse(dueAt, canvasDateTimeFormatter)

            val minutesRemaining = ChronoUnit.MINUTES.between(now, dueDate)
            val hoursRemaining = ChronoUnit.HOURS.between(now, dueDate)
            val daysRemaining = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate())

            when {
                minutesRemaining < 0 -> {
                    val daysPassed = ChronoUnit.DAYS.between(dueDate.toLocalDate(), now.toLocalDate())
                    if (daysPassed > 0) {
                        RemainingTime.Overdue(daysPassed)
                    } else {
                        RemainingTime.Closed
                    }
                }
                minutesRemaining < 60 -> RemainingTime.Minutes(minutesRemaining)
                hoursRemaining < 24 -> RemainingTime.Hours(hoursRemaining)
                else -> RemainingTime.Days(daysRemaining)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            RemainingTime.Error
        }
    }
}