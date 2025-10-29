package com.skku_team2.skku_helper.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


data class LogInUiState(
    val token: String = "",
    val buttonLogInEnabled: Boolean = false,
    // TODO: Loading, Error
)

class LogInViewModel : ViewModel() {
    private val _uiState = MutableLiveData(LogInUiState())
    val uiState: LiveData<LogInUiState> = _uiState

    fun onTokenChanged(text: String) {
        _uiState.value = _uiState.value?.copy(
            token = text,
            buttonLogInEnabled = text.isNotBlank()
        )
    }
}
