package com.skku_team2.skku_helper.ui.main

import androidx.lifecycle.ViewModel
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.utils.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * CalendarFragment 단위 ViewModel
 */

/**
 * CalendarFragment UI 상태 저장 클래스
 */
data class CalendarUiState(
    val selectedDate: LocalDate? = LocalDate.now(),
    val selectedDateAssignmentDataList: List<AssignmentData> = emptyList()
)

/**
 * CalendarFragment 단위 ViewModel
 */
class CalendarViewModel : ViewModel() {
    // UI 상태 관리
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    /**
     * 필터링 날짜 지정 함수
     */
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

    /**
     * List<AssignmentData>의 날짜 목록 추출 함수
     */
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
