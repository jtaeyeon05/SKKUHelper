package com.skku_team2.skku_helper.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtil {
    // Canvas API의 날짜 형식 (ISO 8601)
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun calculateRemainingDays(dueAt: String?): String {
        if (dueAt == null) {
            return "기한 없음"
        }

        return try {
            val dueDate = OffsetDateTime.parse(dueAt, formatter)
            val now = OffsetDateTime.now()
            val minutesRemaining = ChronoUnit.MINUTES.between(now, dueDate)

            when {
                minutesRemaining < 0 -> "마감"
                minutesRemaining < 60 -> "${minutesRemaining}분 남음"
                else -> {
                    val hoursRemaining = ChronoUnit.HOURS.between(now, dueDate)
                    if (hoursRemaining < 24) {
                        "${hoursRemaining}시간 남음"
                    } else {
                        val daysRemaining = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate())
                        "D-${daysRemaining}"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "날짜 오류"
        }
    }
}