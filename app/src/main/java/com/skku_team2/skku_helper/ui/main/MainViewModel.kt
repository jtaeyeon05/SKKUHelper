package com.skku_team2.skku_helper.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasClient
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.map


class MainRepository {
    suspend fun getAssignmentDataList(
        token: String,
        showOnlyThisSemester: Boolean = true
    ): List<AssignmentData>? {
        return withContext(Dispatchers.IO) {
            val authorizationToken = "Bearer $token"

            val courseList = CanvasClient.api.getCourses(authorizationToken).execute().body()
            Log.d("CanvasRepository", "[assignmentDataListFlow] courseList: ${courseList?.size}")

            val assignmentDataList = coroutineScope {
                courseList?.map { course ->
                    val remainingSeconds = DateUtil.calculateRemainingTime(course.createdAt).remainingSeconds
                    async {
                        if (showOnlyThisSemester && -(remainingSeconds ?: Long.MIN_VALUE) > 180 * 24 * 60 * 60) {
                            emptyList()
                        } else {
                            val assignmentList = CanvasClient.api.getAssignments(authorizationToken, course.id).execute().body()
                            Log.d("CanvasRepository", "[assignmentDataListFlow] assignmentList (${course.name}): ${assignmentList?.size}")
                            assignmentList?.map { assignment ->
                                AssignmentData(
                                    course = course,
                                    assignment = assignment
                                )
                            } ?: emptyList()
                        }
                    }
                }
            }?.awaitAll()?.flatten()

            assignmentDataList?.sortedWith(compareBy(nullsLast()) { it.assignment.dueAt })
        }
    }

    fun getAssignmentDataListFlow(token: String, showOnlyThisSemester: Boolean = true) = flow {
        val authorizationToken = "Bearer $token"

        val courseList = CanvasClient.api.getCourses(authorizationToken).execute().body()
        Log.d("CanvasRepository", "[assignmentDataListFlow] courseList: ${courseList?.size}")
        if (courseList == null || courseList.isEmpty()) {
            emit(Result.success(emptyList()))
            return@flow
        }

        val assignmentDataList = coroutineScope {
            courseList.map { course ->
                val remainingSeconds = DateUtil.calculateRemainingTime(course.createdAt).remainingSeconds
                if (showOnlyThisSemester && remainingSeconds != null && -remainingSeconds > 180 * 24 * 60 * 60) {
                    async { emptyList() }
                } else {
                    async {
                        val assignmentList = CanvasClient.api.getAssignments(authorizationToken, course.id).execute().body()
                        Log.d("CanvasRepository", "[assignmentDataListFlow] assignmentList (${course.name}): ${assignmentList?.size}")
                        assignmentList?.map { assignment ->
                            AssignmentData(
                                course = course,
                                assignment = assignment
                            )
                        } ?: emptyList()
                    }
                }
            }.awaitAll().flatten()
        }

        val sortedList = assignmentDataList.sortedWith(compareBy(nullsLast()) { it.assignment.dueAt })
        emit(Result.success(sortedList))
    }.catch { e ->
        Log.e("CanvasRepository", "[assignmentDataListFlow] error: $e")
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)
}

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: ""

    private val repository = MainRepository()
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
}
