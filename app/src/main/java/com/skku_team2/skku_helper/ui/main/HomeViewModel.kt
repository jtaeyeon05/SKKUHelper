package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class HomeUiState(
    val selectedCourseId: Int? = null,
    val isLeftAssignmentExpanded: Boolean = true,
    val isCompletedAssignmentExpanded: Boolean = true,
    val isExpiredAssignmentExpanded: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun selectCourse(courseId: Int?) {
        _uiState.update {
            it.copy(selectedCourseId = courseId)
        }
    }

    fun toggleLeftAssignment() = setLeftAssignmentExpanded(!uiState.value.isLeftAssignmentExpanded)
    fun setLeftAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isLeftAssignmentExpanded = isExpanded)
        }
    }

    fun toggleCompletedAssignment() = setCompletedAssignmentExpanded(!uiState.value.isCompletedAssignmentExpanded)
    fun setCompletedAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isCompletedAssignmentExpanded = isExpanded)
        }
    }

    fun toggleExpiredAssignment() =setExpiredAssignmentExpanded(!uiState.value.isExpiredAssignmentExpanded)
    fun setExpiredAssignmentExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(isExpiredAssignmentExpanded = isExpanded)
        }
    }
}
