package com.skku_team2.skku_helper.utils

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * 날짜 및 시간 관련 처리를 담당하는 유틸리티 객체
 */

object DateUtil {
    // Canvas LMS API 표준 날짜 포멧
    private val canvasDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    data class DateResult(
        val remainingSeconds: Long?,
        val type: Type
    ) { enum class Type { UPCOMING, OVERDUE, NO_DATA, ERROR } }

    /**
     * Canvas LMS API 표준 날짜 포멧으로부터 OffsetDateTime 객체로 변환
     * 파싱 실패 시 null을 반환
     */
    fun parseOffsetDateTime(time: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(time, canvasDateTimeFormatter)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Canvas LMS API 표준 날짜 포멧으로부터 LocalDate 객체로 변환
     * 파싱 실패 시 null을 반환
     */
    fun parseLocalDate(time: String): LocalDate? {
        return try {
            OffsetDateTime.parse(time, canvasDateTimeFormatter).toLocalDate()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * 마감일 문자열을 받아 현재 시간과의 차이를 계산
     */
    fun calculateRemainingTime(dueAt: String?): DateResult {
        if (dueAt == null) return DateResult(null, DateResult.Type.NO_DATA)

        return try {
            val now = OffsetDateTime.now()
            val dueDate = OffsetDateTime.parse(dueAt, canvasDateTimeFormatter)

            val diffInSeconds = ChronoUnit.SECONDS.between(now, dueDate)
            val type = if (diffInSeconds >= 0) DateResult.Type.UPCOMING else DateResult.Type.OVERDUE

            DateResult(diffInSeconds, type)
        } catch (e: Exception) {
            e.printStackTrace()
            DateResult(null, DateResult.Type.ERROR)
        }
    }

    /**
    * 초 단위의 시간을 입력받아 사용자 친화적인 문자열로 변환
    */
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