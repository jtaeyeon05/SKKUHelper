package com.skku_team2.skku_helper.canvas

import com.google.gson.annotations.SerializedName
import com.skku_team2.skku_helper.utils.DateUtil


data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sortable_name") val sortableName: String? = null,
    @SerializedName("short_code") val shortName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,

    @SerializedName("locale") val locale: String? = null,
    @SerializedName("effective_locale") val effectiveLocale: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("permissions") val permissions: Permissions? = null,

    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("last_login") val lastLogin: String? = null,
) {
    data class Permissions(
        @SerializedName("can_update_name") val canUpdateName: Boolean? = null,
        @SerializedName("can_update_avatar") val canUpdateAvatar: Boolean? = null,
        @SerializedName("limit_parent_app_web_access") val hasLimitParentAppWebAccess: Boolean? = null,
    ) {
        val default get() = Permissions()
    }

    companion object {
        val default get() = User(
            id = 0,
            name = "Name"
        )
    }
}


data class Course(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("course_code") val courseCode: String?,

    @SerializedName("uuid") val uuid: String,
    @SerializedName("account_id") val accountId: Int,

    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("start_at") val startAt: String?,
    @SerializedName("end_at") val endAt: String?,

    /* Additional Fields
    @SerializedName("grading_standard_id") val gradingStandardId: Int?,
    @SerializedName("default_view") val defaultView: String,
    @SerializedName("root_account_id") val rootAccountId: Int,
    @SerializedName("enrollment_term_id") val enrollmentTermId: Int?,
    @SerializedName("license") val license: String,
    @SerializedName("grade_passback_setting") val gradePassbackSetting: String?,
    @SerializedName("public_syllabus") val publicSyllabus: Boolean,
    @SerializedName("public_syllabus_to_auth") val publicSyllabusToAuth: Boolean,
    @SerializedName("storage_quota_mb") val storageQuotaMb: Int,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("is_public_to_auth_users") val isPublicToAuthUsers: Boolean,
    @SerializedName("apply_assignment_group_weights") val applyAssignmentGroupWeights: Boolean,
    @SerializedName("time_zone") val timeZone: String,
    @SerializedName("blueprint") val blueprint: Boolean,
    @SerializedName("hide_final_grades") val hideFinalGrades: Boolean,
    @SerializedName("workflow_state") val workflowState: String,
    @SerializedName("course_format") val courseFormat: String,
    @SerializedName("restrict_enrollments_to_course_dates") val restrictEnrollmentsToCourseDates: Boolean,
    @SerializedName("overridden_course_visibility") val overriddenCourseVisibility: String?
     */
) {
    companion object {
        val default get() = Course(
            id = 0,
            name = "Course",
            originalName = "Course",
            courseCode = null,
            uuid = "ABCDEFG",
            accountId = 0,
            createdAt = null,
            startAt = null,
            endAt = null
        )
    }
}


data class Assignment(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,

    @SerializedName("position") val position: Int? = null,
    @SerializedName("course_id") val courseId: Int? = null,
    @SerializedName("is_quiz_assignment") val isQuizAssignment: Boolean,
    @SerializedName("grading_type") val gradingType: String? = null,
    @SerializedName("points_possible") val pointsPossible: Double? = null,
    @SerializedName("locked_for_user") val lockedForUser: Boolean? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,

    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("due_at") val dueAt: String? = null,
    @SerializedName("lock_at") val lockAt: String? = null,
    @SerializedName("unlock_at") val unlockAt: String? = null,

    @SerializedName("submission") val submission: Submission? = null,

    /* Additional Fields
    @SerializedName("assignment_group_id") val assignmentGroupId: Int?,
    @SerializedName("grading_standard_id") val gradingStandardId: Int?,
    @SerializedName("peer_reviews") val peerReviews: Boolean,
    @SerializedName("automatic_peer_reviews") val automaticPeerReviews: Boolean,
    @SerializedName("grade_group_students_individually") val gradeGroupStudentsIndividually: Boolean,
    @SerializedName("anonymous_peer_reviews") val anonymousPeerReviews: Boolean,
    @SerializedName("group_category_id") val groupCategoryId: Int?,
    @SerializedName("post_to_sis") val postToSis: Boolean,
    @SerializedName("moderated_grading") val moderatedGrading: Boolean,
    @SerializedName("omit_from_final_grade") val omitFromFinalGrade: Boolean,
    @SerializedName("intra_group_peer_reviews") val intraGroupPeerReviews: Boolean,
    @SerializedName("anonymous_instructor_annotations") val anonymousInstructorAnnotations: Boolean,
    @SerializedName("anonymous_grading") val anonymousGrading: Boolean,
    @SerializedName("graders_anonymous_to_graders") val gradersAnonymousToGraders: Boolean,
    @SerializedName("grader_count") val graderCount: Int,
    @SerializedName("grader_comments_visible_to_graders") val graderCommentsVisibleToGraders: Boolean,
    @SerializedName("final_grader_id") val finalGraderId: Int?,
    @SerializedName("grader_names_visible_to_final_grader") val graderNamesVisibleToFinalGrader: Boolean,
    @SerializedName("allowed_attempts") val allowedAttempts: Int,
    @SerializedName("secure_params") val secureParams: String?,
    @SerializedName("submission_types") val submissionTypes: List<String>,
    @SerializedName("has_submitted_submissions") val hasSubmittedSubmissions: Boolean,
    @SerializedName("due_date_required") val dueDateRequired: Boolean,
    @SerializedName("max_name_length") val maxNameLength: Int,
    @SerializedName("in_closed_grading_period") val inClosedGradingPeriod: Boolean,
    @SerializedName("can_duplicate") val canDuplicate: Boolean,
    @SerializedName("original_course_id") val originalCourseId: Int?,
    @SerializedName("original_assignment_id") val originalAssignmentId: Int?,
    @SerializedName("original_assignment_name") val originalAssignmentName: String?,
    @SerializedName("original_quiz_id") val originalQuizId: Int?,
    @SerializedName("workflow_state") val workflowState: String,
    @SerializedName("muted") val muted: Boolean,
    @SerializedName("published") val published: Boolean,
    @SerializedName("only_visible_to_overrides") val onlyVisibleToOverrides: Boolean,
    @SerializedName("submissions_download_url") val submissionsDownloadUrl: String?,
     */
) {
    enum class Status { Left, Completed, Expired }

    companion object {
        val default get() = Assignment(
            id = 0,
            name = "Assignment",
            description = "Assignment Description",
            position = 1,
            courseId = 0,
            isQuizAssignment = false
        )
    }

    val isSubmitted get() = submission?.workflowState == "submitted" || submission?.workflowState == "graded" || submission?.workflowState == "pending_review"
    val status: Status get() {
        val remainingTime = DateUtil.calculateRemainingTime(dueAt)
        return if (isSubmitted) Status.Completed
        else if (remainingTime.type == DateUtil.DateResult.Type.UPCOMING) Status.Left
        else Status.Expired
    }
}

data class Submission(
    @SerializedName("id") val id: Int,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("workflow_state") val workflowState: String,
    @SerializedName("submission_type") val submissionType: String, // online_quiz, online_upload..
    @SerializedName("late") val late: Boolean,

    @SerializedName("body") val body: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("preview_url") val previewUrl: String? = null,

    @SerializedName("submitted_at") val submittedAt: String? = null,
    @SerializedName("graded_at") val gradedAt: String? = null,
    @SerializedName("posted_at") val postedAt: String? = null,

    @SerializedName("assignment_id") val assignmentId: Int,
    @SerializedName("user_id") val userId: Int,

    @SerializedName("attempt") val attempt: Int? = null,
    @SerializedName("extra_attempts") val extraAttempts: Int? = null,
    @SerializedName("attachments") val attachments: List<Attachment>? = null,

    /* Additional Fields
    @SerializedName("grade_matches_current_submission") val gradeMatchesCurrentSubmission: Boolean,
    @SerializedName("grader_id") val graderId: Int?,
    @SerializedName("cached_due_date") val cachedDueDate: String?,
    @SerializedName("excused") val excused: Boolean?,
    @SerializedName("late_policy_status") val latePolicyStatus: String?,
    @SerializedName("points_deducted") val pointsDeducted: Double?,
    @SerializedName("grading_period_id") val gradingPeriodId: Int?,
    @SerializedName("missing") val missing: Boolean,
    @SerializedName("seconds_late") val secondsLate: Int,
    @SerializedName("entered_grade") val enteredGrade: String?,
    @SerializedName("entered_score") val enteredScore: Double?,
    */
) {
    data class Attachment(
        @SerializedName("id") val id: Int,
        @SerializedName("uuid") val uuid: String? = null,
        @SerializedName("folder_id") val folderId: Int? = null,
        @SerializedName("display_name") val displayName: String,
        @SerializedName("filename") val filename: String,

        @SerializedName("upload_status") val uploadStatus: String? = null,
        @SerializedName("content-type") val contentType: String? = null, // application/pdf, application/zip..
        @SerializedName("mime_class") val mimeClass: String? = null, // pdf, zip..
        @SerializedName("url") val url: String? = null, // Download Url
        @SerializedName("preview_url") val previewUrl: String? = null,
        @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
        @SerializedName("media_entry_id") val mediaEntryId: String? = null,
        @SerializedName("size") val size: Long? = null, // Bytes

        @SerializedName("created_at") val createdAt: String? = null,
        @SerializedName("updated_at") val updatedAt: String? = null,
        @SerializedName("lock_at") val lockAt: String? = null,
        @SerializedName("unlock_at") val unlockAt: String? = null,
        @SerializedName("modified_at") val modifiedAt: String? = null,

        @SerializedName("locked") val locked: Boolean? = null,
        @SerializedName("hidden") val hidden: Boolean? = null,
        @SerializedName("hidden_for_user") val hiddenForUser: Boolean? = null,
        @SerializedName("locked_for_user") val lockedForUser: Boolean? = null,
    ) {
        companion object {
            val default get() = Attachment(
                id = 0,
                displayName = "Attachment",
                filename = "Attachment"
            )
        }
    }

    companion object {
        val default get() = Submission(
            id = 0,
            grade = "0.0",
            score = 0.0,
            workflowState = "graded",
            submissionType = "online_upload",
            late = false,

            assignmentId = 0,
            userId = 0
        )
    }
}
