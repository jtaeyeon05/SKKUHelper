package com.skku_team2.skku_helper.ui.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.canvas.CanvasClient
import com.skku_team2.skku_helper.canvas.Course
import com.skku_team2.skku_helper.canvas.User
import com.skku_team2.skku_helper.key.IntentKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AssignmentRepository {
    suspend fun getUserSelf(token: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val courseResponse = CanvasClient.api.getUserSelf(authorizationToken).execute()
                if (courseResponse.isSuccessful) courseResponse.body()
                else null
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun getCourse(token: String, courseId: Int): Course? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val courseResponse = CanvasClient.api.getCourse(authorizationToken, courseId).execute()
                if (courseResponse.isSuccessful) courseResponse.body()
                else null
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun getAssignment(token: String, courseId: Int, assignmentId: Int): Assignment? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val assignmentResponse = CanvasClient.api.getAssignment(authorizationToken, courseId, assignmentId).execute()
                if (assignmentResponse.isSuccessful) assignmentResponse.body()
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
    private val _userState = MutableStateFlow<User?>(null)
    private val _courseState = MutableStateFlow<Course?>(null)
    private val _assignmentState = MutableStateFlow<Assignment?>(null)
    private val _uiState = MutableStateFlow(AssignmentUiState())

    val userState: StateFlow<User?> = _userState.asStateFlow()
    val courseState: StateFlow<Course?> = _courseState.asStateFlow()
    val assignmentState: StateFlow<Assignment?> = _assignmentState.asStateFlow()
    val uiState: StateFlow<AssignmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            update()
        }
    }

    suspend fun update() {
        if (token != null && courseId != null && assignmentId != null) {
            val user = repository.getUserSelf(token)
            val course = repository.getCourse(token, courseId)
            val assignment = repository.getAssignment(token, courseId, assignmentId)
            _userState.update { user }
            _courseState.update { course }
            _assignmentState.update { assignment }
        }
        // TODO: UiState, FireBase
    }
}
