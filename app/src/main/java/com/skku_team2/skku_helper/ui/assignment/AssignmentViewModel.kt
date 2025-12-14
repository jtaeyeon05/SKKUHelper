package com.skku_team2.skku_helper.ui.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasRepository
import com.skku_team2.skku_helper.canvas.CustomAssignmentData
import com.skku_team2.skku_helper.key.IntentKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * AssignmentActivity 단위 ViewModel
 */

class AssignmentViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    // SavedStateHandle에서 Token, CourseId, AssignmentId를 불러옴
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN)
    private val courseId = savedStateHandle.get<Int>(IntentKey.EXTRA_COURSE_ID)
    private val assignmentId = savedStateHandle.get<Int>(IntentKey.EXTRA_ASSIGNMENT_ID)

    // CanvasRepository 객체 생성
    private val repository = CanvasRepository()

    // UI 상태 관리
    private val _assignmentDataState = MutableStateFlow<AssignmentData?>(null)
    val assignmentDataState: StateFlow<AssignmentData?> = _assignmentDataState.asStateFlow()

    init {
        viewModelScope.launch {
            update()
        }
    }

    /**
     * 데이터 새로고침 함수
     */
    suspend fun update() {
        if (token != null && courseId != null && assignmentId != null) {
            val assignmentData = repository.getAssignmentData(token, courseId, assignmentId)
            _assignmentDataState.update { assignmentData }
        }
    }

    /**
     * 과제 삭제 함수
     */
    fun deleteAssignment() {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = assignmentDataState.value?.custom ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(isDeleted = true)
        _assignmentDataState.update { it?.copy(custom = updatedCustomAssignmentData) }

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
     * 삭제 과제 복구 함수 (미사용)
     */
    /*
    fun restoreAssignment() {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = assignmentDataState.value?.custom ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(isDeleted = false)
        _assignmentDataState.update { it?.copy(custom = updatedCustomAssignmentData) }

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }
    */

    /**
     * 과제 메모 작성 함수
     */
    fun changeAssignmentMemo(memo: String) {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = assignmentDataState.value?.custom ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(memo = memo)
        _assignmentDataState.update { it?.copy(custom = updatedCustomAssignmentData) }

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
     * 커스텀 과제 데이터 변경 함수
     */
    fun changeAssignmentData(
        name: String? = null,
        dueAt: String? = null
    ) {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = assignmentDataState.value?.custom ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(
            name = name.let { if (it?.trim().isNullOrBlank()) null else it.trim() },
            dueAt = dueAt.let { if (it?.trim().isNullOrBlank()) null else it.trim() }
        )
        _assignmentDataState.update { it?.copy(custom = updatedCustomAssignmentData) }

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }
}
