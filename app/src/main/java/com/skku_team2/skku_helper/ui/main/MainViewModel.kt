package com.skku_team2.skku_helper.ui.main

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


data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: ""

    private val repository = CanvasRepository()
    private val _assignmentDataListState = MutableStateFlow<List<AssignmentData>?>(null)
    private val _uiState = MutableStateFlow(MainUiState())

    val assignmentDataListState = _assignmentDataListState.asStateFlow()
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetch()
        }
    }

    suspend fun fetch() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        _assignmentDataListState.update { repository.getAssignmentDataList(token) }
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
}
