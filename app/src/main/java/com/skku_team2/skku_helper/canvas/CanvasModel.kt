package com.skku_team2.skku_helper.canvas

import com.skku_team2.skku_helper.utils.DateUtil

/**
 * 과제 정보 저장 모델
 */

/**
 * 과제 정보 클래스
 */
data class AssignmentData(
    val course: Course,
    val assignment: Assignment,
    val custom: CustomAssignmentData?
) {
    enum class Status { Left, Completed, Expired }

    val isSubmitted get() = assignment.submission?.workflowState == "submitted" || assignment.submission?.workflowState == "graded" || assignment.submission?.workflowState == "pending_review"
    val status: Status get() {
        val remainingTime = DateUtil.calculateRemainingTime(custom?.dueAt ?: assignment.dueAt)
        return if (isSubmitted) Status.Completed
        else if (remainingTime.type == DateUtil.DateResult.Type.UPCOMING) Status.Left
        else Status.Expired
    }
}

/**
 * Firebase Firestore 기반 커스텀 과제 정보 저장 클래스
 */
data class CustomAssignmentData(
    val name: String? = null,
    val dueAt: String? = null,
    val memo: String? = null,
    @field:JvmField val isDeleted: Boolean? = null
)
