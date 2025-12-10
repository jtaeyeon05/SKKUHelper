package com.skku_team2.skku_helper.canvas

import android.content.Context
import android.util.Log
import com.skku_team2.skku_helper.key.PrefKey
import com.skku_team2.skku_helper.utils.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


// TODO: Move to MainViewModel, token from savedStateHandle
class CanvasRepository(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(PrefKey.Settings.key, Context.MODE_PRIVATE)
    private val client = CanvasClient.api

    fun getAssignmentDataListFlow(showOnlyThisSemester: Boolean = true) = flow {
        val token = sharedPreferences.getString(PrefKey.Settings.TOKEN, null)
        Log.d("CanvasRepository", "[assignmentDataListFlow] Token: $token")
        if (token.isNullOrBlank()) {
            emit(Result.failure(Exception("Failed on Token Loading.")))
            return@flow
        }

        val apiToken = "Bearer $token"
        val courseList = client.getCourses(apiToken).execute().body()
        Log.d("CanvasRepository", "[assignmentDataListFlow] courseList: ${courseList?.size}")
        if (courseList == null || courseList.isEmpty()) {
            emit(Result.success(emptyList()))
            return@flow
        }

        val assignmentDataList = coroutineScope {
            courseList.map { course ->
                val remainingSeconds = DateUtil.calculateRemainingTime(course.createdAt).remainingSeconds
                if (showOnlyThisSemester && remainingSeconds != null && -remainingSeconds > 180 * 24 * 60 * 60) {
                    async {
                        emptyList()
                    }
                } else {
                    async {
                        val assignmentList = client.getAssignments(apiToken, course.id).execute().body()
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
