package com.skku_team2.skku_helper.ui.start

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.CanvasClient
import com.skku_team2.skku_helper.key.PrefKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StartRepository(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(PrefKey.Settings.key, Context.MODE_PRIVATE)

    suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putString(PrefKey.Settings.TOKEN, token)
            }
        }
    }

    suspend fun loadToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(PrefKey.Settings.TOKEN, null)
        }
    }

    suspend fun verifyToken(token: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val coursesResponse = CanvasClient.api.getCourses(authorizationToken, 5).execute()
                coursesResponse.isSuccessful
            } catch (_: Exception) {
                false
            }
        }
    }
}

data class StartUiState(
    val token: String? = null,
    val isTokenVerified: Boolean? = null,
)

class StartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StartRepository(application)
    private val _uiState = MutableStateFlow(StartUiState())

    val uiState: StateFlow<StartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val token = repository.loadToken()
            _uiState.update { it.copy(token = token) }

            if (token == null) {
                _uiState.update { it.copy(isTokenVerified = false) }
            } else {
                val isTokenVerified = repository.verifyToken(token)
                _uiState.update { it.copy(isTokenVerified = isTokenVerified) }
            }
        }
    }

    fun changeToken(token: String) {
        _uiState.update {
            it.copy(
                token = token,
                isTokenVerified = null,
            )
        }
    }

    suspend fun verifyToken(): Boolean {
        val token = uiState.value.token ?: ""
        val isTokenVerified = repository.verifyToken(token)
        if (isTokenVerified) repository.saveToken(token)
        _uiState.update {
            it.copy(
                token = token,
                isTokenVerified = isTokenVerified,
            )
        }
        return isTokenVerified
    }
}
