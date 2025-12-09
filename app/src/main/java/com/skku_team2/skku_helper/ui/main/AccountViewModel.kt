package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.CanvasRepository
import com.skku_team2.skku_helper.canvas.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val userProfile: UserProfile = UserProfile.default,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CanvasRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AccountUiState())

    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.userProfileFlow.collect { result ->
                result.onSuccess { profile ->
                    _uiState.update {
                        it.copy(isLoading = false, userProfile = profile)
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
            }
        }
    }
}