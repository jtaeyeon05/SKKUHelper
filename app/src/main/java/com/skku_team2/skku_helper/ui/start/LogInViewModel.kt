package com.skku_team2.skku_helper.ui.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay


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

    suspend fun logIn(token: String = uiState.value?.token.orEmpty()): Boolean {
        // TODO: Log In
        delay(1000)
        return true
    }
}
