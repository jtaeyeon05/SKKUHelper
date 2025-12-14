package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasRepository
import com.skku_team2.skku_helper.canvas.CustomAssignmentData
import com.skku_team2.skku_helper.canvas.Profile
import com.skku_team2.skku_helper.key.IntentKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MainActivity 단위 ViewModel
 */

/**
 * MainActivity UI 상태 저장 클래스
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * MainActivity 단위 ViewModel
 */
class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    // SavedStateHandle에서 Token를 불러옴
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: ""

    // MainRepository 객체 생성
    private val repository = CanvasRepository()

    // UI 상태 관리
    private val _assignmentDataListState = MutableStateFlow<List<AssignmentData>?>(null)
    private val _userState = MutableStateFlow<Profile?>(null)
    private val _uiState = MutableStateFlow(MainUiState())
    val assignmentDataListState = _assignmentDataListState.asStateFlow()
    val userState: StateFlow<Profile?> = _userState.asStateFlow()
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            update()
        }
    }

    /**
     * 데이터 새로고침 함수
     */
    suspend fun update() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        _assignmentDataListState.update { repository.getAssignmentDataList(token) }
        _userState.update { repository.getProfileSelf(token) }
        if (assignmentDataListState.value == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed on Fetching Data."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * 과제 삭제 함수
     */
    fun deleteAssignment(courseId: Int, assignmentId: Int) {
        val assignmentData = assignmentDataListState.value?.find { assignmentData ->
            assignmentData.course.id == courseId && assignmentData.assignment.id == assignmentId
        }
        val currentCustomAssignmentData = assignmentData?.custom ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(isDeleted = true)

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }

    /**
     * 커스템 과제 데이터 삭제 함수
     */
    suspend fun invalidateFirebaseData() {
        repository.invalidateFirebaseData(token)
    }
}
