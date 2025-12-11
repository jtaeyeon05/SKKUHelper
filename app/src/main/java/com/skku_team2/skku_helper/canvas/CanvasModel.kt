package com.skku_team2.skku_helper.canvas


data class AssignmentData(
    val course: Course,
    val assignment: Assignment,
    val custom: CustomAssignmentData?
)

data class CustomAssignmentData(
    val name: String? = null,
    val dueAt: String? = null,
    val memo: String? = null,
    @field:JvmField val isDeleted: Boolean? = null
)

