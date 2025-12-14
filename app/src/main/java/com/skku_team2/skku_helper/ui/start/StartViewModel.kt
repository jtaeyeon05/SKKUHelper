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

/**
 * StartActivity 단위 ViewModel
 */

/**
 * StartViewModel을 위한 데이터 레이어
 */
class StartRepository(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(PrefKey.Settings.key, Context.MODE_PRIVATE)

    /**
     * Canvas LMS API 토큰 저장 함수
     */
    suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putString(PrefKey.Settings.TOKEN, token)
            }
        }
    }

    /**
     * Canvas LMS API 토큰 조회 함수
     */
    suspend fun loadToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(PrefKey.Settings.TOKEN, null)
        }
    }

    /**
     * Canvas LMS API 토큰 검증 함수
     */
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

/**
 * StartActivity UI 상태 저장 클래스
 */
data class StartUiState(
    val token: String? = null,
    val isTokenVerified: Boolean? = null,
)

/**
 * StartActivity 단위 ViewModel
 */
class StartViewModel(application: Application) : AndroidViewModel(application) {
    // StartRepository 객체 생성
    private val repository = StartRepository(application)

    // UI 상태 관리
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

    /**
     * 토큰 변경 함수
     */
    fun changeToken(token: String) {
        _uiState.update {
            it.copy(
                token = token,
                isTokenVerified = null,
            )
        }
    }

    /**
     * 토큰 검증 함수
     */
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
