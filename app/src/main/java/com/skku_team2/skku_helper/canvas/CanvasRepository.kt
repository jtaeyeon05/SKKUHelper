package com.skku_team2.skku_helper.canvas

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.skku_team2.skku_helper.utils.DateUtil
import com.skku_team2.skku_helper.utils.toSha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*
 * 데이터 로직 레이어
 */

class CanvasRepository {
    // 파이어베이스 통신 인스턴스
    private val db = FirebaseFirestore.getInstance()

    /**
     * 사용자가 수강하는 모든 코스의 모든 과제를 조회
     */
    suspend fun getAssignmentDataList(
        token: String,
        showOnlyThisSemester: Boolean = true
    ): List<AssignmentData>? {
        return withContext(Dispatchers.IO) {
            val authorizationToken = "Bearer $token"

            val courseList = CanvasClient.api.getCourses(authorizationToken).execute().body()
            Log.d("CanvasRepository", "[assignmentDataListFlow] courseList: ${courseList?.size}")

            coroutineScope {
                courseList?.map { course ->
                    val remainingSeconds = DateUtil.calculateRemainingTime(course.createdAt).remainingSeconds
                    async {
                        if (showOnlyThisSemester && -(remainingSeconds ?: Long.MIN_VALUE) > 180 * 24 * 60 * 60) {
                            emptyList()
                        } else {
                            val assignmentList = CanvasClient.api.getAssignments(authorizationToken, course.id).execute().body()
                            Log.d("CanvasRepository", "[assignmentDataListFlow] assignmentList (${course.name}): ${assignmentList?.size}")
                            assignmentList?.map { assignment ->
                                val custom = getCustomAssignmentData(token, course.id, assignment.id)
                                AssignmentData(
                                    course = course,
                                    assignment = assignment,
                                    custom = custom
                                )
                            } ?: emptyList()
                        }
                    }
                }
            }?.awaitAll()?.flatten()
        }
    }

    /**
     * 사용자의 프로필을 조회
     */
    suspend fun getProfileSelf(token: String): Profile? {
        return withContext(Dispatchers.IO) {
            try {
                val authorizationToken = "Bearer $token"
                val response = CanvasClient.api.getProfileSelf(authorizationToken).execute()
                if (response.isSuccessful) {
                    Log.d("CanvasRepository", "[getProfileSelf] Success: ${response.body()?.name}")
                    response.body()
                } else {
                    Log.e("CanvasRepository", "[getProfileSelf] Failed: ${response.code()}")
                    null
                }
            } catch (exception: Exception) {
                Log.e("CanvasRepository", "[getProfileSelf] Error: $exception")
                null
            }
        }
    }

    /**
     * 사용자가 수강하는 특정 코스의 특정 과제를 조회
     */
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
                val customAssignmentData = getCustomAssignmentData(token, courseId, assignmentId)
                if (course != null && assignment != null) AssignmentData(course, assignment, customAssignmentData)
                else null
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * 사용자가 수강하는 특정 코스의 특정 과제의 커스텀 데이터를 조회
     */
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

    /**
     * 사용자가 수강하는 특정 코스의 특정 과제의 커스텀 데이터를 저장
     */
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

    /**
     * 커스텀 데이터를 초기화
     */
    suspend fun invalidateFirebaseData(token: String) {
        withContext(Dispatchers.IO) {
            val hashedToken = token.toSha256()
            val parentDocRef = db.collection("userData").document(hashedToken)
            val subDocRef = parentDocRef.collection("customAssignmentData")
            val snapshot = subDocRef.get().await()

            val batch = db.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            if (!snapshot.isEmpty) {
                batch.commit().await()
            }
            parentDocRef.delete().await()
        }
    }
}
