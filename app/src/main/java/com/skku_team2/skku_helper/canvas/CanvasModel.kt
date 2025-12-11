package com.skku_team2.skku_helper.canvas


data class AssignmentData(
    val course: Course,
    val assignment: Assignment,
)

data class CustomAssignmentData(
    val name: String? = null,
    val dueAt: String? = null,
    val memo: String? = null,
    val isDeleted: Boolean? = null
)
