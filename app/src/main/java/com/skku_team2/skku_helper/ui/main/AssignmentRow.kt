package com.skku_team2.skku_helper.ui.main

sealed class AssignmentRow {
    data class Header(
        val title: String,
        var expanded: Boolean
    ) : AssignmentRow()

    data class Item(
        val assignment: Assignment
    ) : AssignmentRow()
}