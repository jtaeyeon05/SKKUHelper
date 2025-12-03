package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasRepository
import com.skku_team2.skku_helper.key.IntentKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MainUiState(
    val assignmentDataList: List<AssignmentData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    private val repository = CanvasRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(MainUiState())

    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: ""

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
}
