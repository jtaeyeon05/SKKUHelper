package com.skku_team2.skku_helper.canvas

import com.google.gson.annotations.SerializedName


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

    @SerializedName("position") val position: Int?,
    @SerializedName("course_id") val courseId: Int?,
    @SerializedName("is_quiz_assignment") val isQuizAssignment: Boolean,
    @SerializedName("grading_type") val gradingType: String?,
    @SerializedName("points_possible") val pointsPossible: Double?,
    @SerializedName("locked_for_user") val lockedForUser: Boolean?,
    @SerializedName("html_url") val htmlUrl: String?,

    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("due_at") val dueAt: String?,
    @SerializedName("lock_at") val lockAt: String?,
    @SerializedName("unlock_at") val unlockAt: String?,

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
    companion object {
        val default get() = Assignment(
            id = 0,
            name = "Assignment",
            description = "Assignment Description",
            position = 1,
            courseId = 0,
            isQuizAssignment = false,
            gradingType = null,
            pointsPossible = null,
            lockedForUser = null,
            htmlUrl = null,
            createdAt = null,
            updatedAt = null,
            dueAt = null,
            lockAt = null,
            unlockAt = null
        )
    }
}
