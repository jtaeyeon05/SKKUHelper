package com.skku_team2.skku_helper.ui.main

import androidx.lifecycle.ViewModel
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.utils.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate


data class CalendarUiState(
    val selectedDate: LocalDate? = LocalDate.now(),
    val selectedDateAssignmentDataList: List<AssignmentData> = emptyList()
)


class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())

    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun selectDate(
        selectedDate: LocalDate?,
        assignmentDataList: List<AssignmentData>?
    ) {
        val selectedDateAssignmentDataList = assignmentDataList?.filter { assignmentDate ->
            val date = DateUtil.parseLocalDate(assignmentDate.custom?.dueAt ?: assignmentDate.assignment.dueAt ?: "")
            selectedDate == date
        }?.sortedWith(
            compareBy<AssignmentData> {
                it.status
            }.thenBy(nullsLast()) {
                it.custom?.dueAt ?: it.assignment.dueAt
            }
        )

        _uiState.update {
            it.copy(
                selectedDate = selectedDate,
                selectedDateAssignmentDataList = selectedDateAssignmentDataList ?: emptyList()
            )
        }
    }

    fun getDateList(
        assignmentDataList: List<AssignmentData>
    ): List<LocalDate> {
        return assignmentDataList
            .mapNotNull { assignmentDate ->
                DateUtil.parseLocalDate(assignmentDate.custom?.dueAt ?: assignmentDate.assignment.dueAt ?: "")
            }
            .distinct()
    }
}
