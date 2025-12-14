package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * HomeFragment 단위 ViewModel
 */

/**
 * HomeFragment UI 상태 저장 클래스
 */
data class HomeUiState(
    val selectedCourseId: Int? = null,
    val isLeftAssignmentExpanded: Boolean = true,
    val isCompletedAssignmentExpanded: Boolean = true,
    val isExpiredAssignmentExpanded: Boolean = true
)

/**
 * HomeFragment 단위 ViewModel
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // UI 상태 관리
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * 필터링 코스 지정 함수
     */
    fun selectCourse(courseId: Int?) {
        _uiState.update {
            it.copy(selectedCourseId = courseId)
        }
    }

    /**
     * Left Assignment 펼치기/접기 함수
     */
    fun toggleLeftAssignment() = setLeftAssignmentExpanded(!uiState.value.isLeftAssignmentExpanded)
    fun setLeftAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isLeftAssignmentExpanded = isExpanded)
        }
    }

    /**
     * Completed Assignment 펼치기/접기 함수
     */
    fun toggleCompletedAssignment() = setCompletedAssignmentExpanded(!uiState.value.isCompletedAssignmentExpanded)
    fun setCompletedAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isCompletedAssignmentExpanded = isExpanded)
        }
    }

    /**
     * Expired Assignment 펼치기/접기 함수
     */
    fun toggleExpiredAssignment() =setExpiredAssignmentExpanded(!uiState.value.isExpiredAssignmentExpanded)
    fun setExpiredAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isExpiredAssignmentExpanded = isExpanded)
        }
    }
}
