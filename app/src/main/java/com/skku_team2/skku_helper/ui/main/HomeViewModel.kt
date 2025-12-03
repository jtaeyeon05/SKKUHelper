package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeUiState(
    val assignmentDataList: List<AssignmentData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isLeftAssignmentExpanded: Boolean = true,
    val isCompletedAssignmentExpanded: Boolean = true,
    val isExpiredAssignmentExpanded: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CanvasRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetch()
        }
    }

    suspend fun fetch() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        repository.assignmentDataListFlow
            .collect { result ->
                result
                    .onSuccess { assignmentDataList ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                assignmentDataList = assignmentDataList
                            )
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                assignmentDataList = emptyList(),
                                errorMessage = exception.message ?: "Failed on Fetching Data."
                            )
                        }
                    }
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
