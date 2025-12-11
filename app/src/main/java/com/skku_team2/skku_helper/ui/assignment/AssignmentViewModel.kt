package com.skku_team2.skku_helper.ui.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasClient
import com.skku_team2.skku_helper.key.IntentKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AssignmentRepository {
    suspend fun getAssignmentData(
        token: String,
        courseId: Int,
        assignmentId: Int
    ): AssignmentData? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val course = CanvasClient.api.getCourse(authorizationToken, courseId).execute().body()
                val assignment = CanvasClient.api.getAssignment(authorizationToken, courseId, assignmentId).execute().body()
                if (course != null && assignment != null) AssignmentData(course, assignment)
                else null
            } catch (_: Exception) {
                null
            }
        }
    }
}

data class AssignmentUiState(
    val memo: String? = null
) // TODO: 메모, 수정 사항 등

class AssignmentViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: null
    val courseId = savedStateHandle.get<Int>(IntentKey.EXTRA_COURSE_ID) ?: null
    val assignmentId = savedStateHandle.get<Int>(IntentKey.EXTRA_ASSIGNMENT_ID) ?: null

    private val repository = AssignmentRepository()
    private val _assignmentDataState = MutableStateFlow<AssignmentData?>(null)
    private val _uiState = MutableStateFlow(AssignmentUiState())

    val assignmentDataState: StateFlow<AssignmentData?> = _assignmentDataState.asStateFlow()
    val uiState: StateFlow<AssignmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            update()
        }
    }

    suspend fun update() {
        if (token != null && courseId != null && assignmentId != null) {
            val assignmentData = repository.getAssignmentData(token, courseId, assignmentId)
            _assignmentDataState.update { assignmentData }
        }
        // TODO: UiState, FireBase
    }
}
