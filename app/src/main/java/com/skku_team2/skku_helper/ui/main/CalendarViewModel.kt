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
    val assignmentDataList: List<AssignmentData> = emptyList(),
    val assignmentsByDate: Map<LocalDate, List<AssignmentData>> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateAssignments: List<AssignmentData> = emptyList()
)

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun setAssignments(assignmentDataList: List<AssignmentData>) {
        val currentSelectedDate = _uiState.value.selectedDate

        val groupedByDate: Map<LocalDate, List<AssignmentData>> =
            assignmentDataList
                .groupBy { data ->
                    DateUtil.toLocalDate(data.assignment.dueAt)   // LocalDate?
                }
                .filterKeys { it != null }
                .mapKeys { (key, _) -> key!! }

        val selectedDateAssignments =
            groupedByDate[currentSelectedDate] ?: emptyList()

        _uiState.update { state ->
            state.copy(
                assignmentDataList = assignmentDataList,
                assignmentsByDate = groupedByDate,
                selectedDateAssignments = selectedDateAssignments
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        val assignmentsForDate =
            _uiState.value.assignmentsByDate[date] ?: emptyList()

        _uiState.update {
            it.copy(
                selectedDate = date,
                selectedDateAssignments = assignmentsForDate
            )
        }
    }
}