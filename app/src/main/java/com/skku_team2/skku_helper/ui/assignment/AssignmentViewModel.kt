package com.skku_team2.skku_helper.ui.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.CanvasClient
import com.skku_team2.skku_helper.canvas.CustomAssignmentData
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.utils.toSha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AssignmentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAssignmentData(
        token: String,
        courseId: Int,
        assignmentId: Int
    ): AssignmentData? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val course = CanvasClient.api.getCourse(authorizationToken, courseId).execute().body()
                val assignment = CanvasClient.api.getAssignment(authorizationToken, courseId, assignmentId).execute().body()
                if (course != null && assignment != null) AssignmentData(course, assignment)
                else null
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun getCustomAssignmentData(
        token: String,
        courseId: Int,
        assignmentId: Int
    ): CustomAssignmentData? {
        return withContext(Dispatchers.IO) {
            try {
                val hashedToken = token.toSha256()
                val docId = "${courseId}_${assignmentId}"
                val document = db.collection("userData").document(hashedToken)
                    .collection("customAssignmentData").document(docId)
                    .get().await()
                document.toObject<CustomAssignmentData>()
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun saveCustomAssignmentData(
        token: String,
        courseId: Int,
        assignmentId: Int,
        data: CustomAssignmentData
    ) {
        withContext(Dispatchers.IO) {
            val hashedToken = token.toSha256()
            val docId = "${courseId}_${assignmentId}"
            val docRef = db.collection("userData").document(hashedToken)
                .collection("customAssignmentData").document(docId)
            docRef.set(data, SetOptions.merge()).await()
        }
    }
}


class AssignmentViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN)
    private val courseId = savedStateHandle.get<Int>(IntentKey.EXTRA_COURSE_ID)
    private val assignmentId = savedStateHandle.get<Int>(IntentKey.EXTRA_ASSIGNMENT_ID)

    private val repository = AssignmentRepository()
    private val _assignmentDataState = MutableStateFlow<AssignmentData?>(null)
    private val _customAssignmentDataState = MutableStateFlow<CustomAssignmentData?>(null)

    val assignmentDataState: StateFlow<AssignmentData?> = _assignmentDataState.asStateFlow()
    val customAssignmentDataState: StateFlow<CustomAssignmentData?> = _customAssignmentDataState.asStateFlow()

    init {
        viewModelScope.launch {
            update()
        }
    }

    suspend fun update() {
        if (token != null && courseId != null && assignmentId != null) {
            val assignmentData = repository.getAssignmentData(token, courseId, assignmentId)
            _assignmentDataState.update { assignmentData }

            val customAssignmentData = repository.getCustomAssignmentData(token, courseId, assignmentId)
            _customAssignmentDataState.update { customAssignmentData }
        }
    }

    fun deleteAssignment() {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = customAssignmentDataState.value ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(isDeleted = true)
        _customAssignmentDataState.update { updatedCustomAssignmentData }

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }

    /*
    fun restoreAssignment() {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = customAssignmentDataState.value ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(isDeleted = false)
        _customAssignmentDataState.update { updatedCustomAssignmentData }

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }
    */

    fun changeAssignmentMemo(memo: String) {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = customAssignmentDataState.value ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(memo = memo)
        _customAssignmentDataState.update { updatedCustomAssignmentData }

        viewModelScope.launch {
            repository.saveCustomAssignmentData(
                token = token,
                courseId = courseId,
                assignmentId = assignmentId,
                data = updatedCustomAssignmentData
            )
        }
    }

    fun changeAssignmentData(
        name: String? = null,
        dueAt: String? = null
    ) {
        if (token == null || courseId == null || assignmentId == null) return

        val currentCustomAssignmentData = customAssignmentDataState.value ?: CustomAssignmentData()
        val updatedCustomAssignmentData = currentCustomAssignmentData.copy(
            name = name.let { if (it?.trim().isNullOrBlank()) null else it.trim() },
            dueAt = dueAt.let { if (it?.trim().isNullOrBlank()) null else it.trim() }
        )
        _customAssignmentDataState.update { updatedCustomAssignmentData }

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
