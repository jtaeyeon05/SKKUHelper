package com.skku_team2.skku_helper.ui.main

data class Assignment(
    val id: Long,
    val title: String,
    val course: String,
    val type: String,
    val remainingDays: Int,
    val isCompleted: Boolean
)
