package com.skku_team2.skku_helper.ui.start

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class LogInUiState(
    val token: String = "",
    val buttonLogInEnabled: Boolean = false,
    // TODO: Loading, Error
)

class LogInViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LogInUiState())
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()

    fun onTokenChanged(text: String) {
        _uiState.update {
            it.copy(
                token = text,
                buttonLogInEnabled = text.isNotBlank()
            )
        }
    }

    suspend fun logIn(token: String = uiState.value?.token.orEmpty()): Boolean {
        // TODO: Log In
        delay(1000)
        return true
    }
}
