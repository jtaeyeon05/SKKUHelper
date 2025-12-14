package com.skku_team2.skku_helper.canvas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Canvas LMS API와 통신하기 위한 Retrofit 인터페이스
 */

interface CanvasApi {
    /**
     * 사용자의 프로필 정보를 가져오는 API
    */
    @GET("api/v1/users/self/profile")
    fun getProfileSelf(
        @Header("Authorization") token: String,
        @Query("include[]") include: String = ""
    ): Call<Profile>

    /**
     * 사용자가 수강하는 모든 코스를 가져오는 API
     */
    @GET("api/v1/courses")
    fun getCourses(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 100,
        @Query("enrollment_state") enrollmentState: String = "active",
    ): Call<List<Course>>

    /**
     * 사용자가 수강하는 특정 코스를 가져오는 API
     */
    @GET("api/v1/courses/{courseId}")
    fun getCourse(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Call<Course>

    /**
     * 사용자가 수강하는 특정 코스의 모든 과제를 가져오는 API
     */
    @GET("api/v1/courses/{courseId}/assignments")
    fun getAssignments(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Query("per_page") perPage: Int = 100,
        @Query("include[]") include: String = "submission"
    ): Call<List<Assignment>>

    /**
     * 사용자가 수강하는 특정 코스의 특정 과제를 가져오는 API
     */
    @GET("api/v1/courses/{courseId}/assignments/{assignmentId}")
    fun getAssignment(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Path("assignmentId") assignmentId: Int,
        @Query("include[]") include: String = "submission"
    ): Call<Assignment>

    /**
     * 특정 사용자가 수강하는 특정 코스의 특정 과제의 제출 상태를 가져오는 API
     */
    @GET("api/v1/courses/{courseId}/assignments/{assignmentId}/submissions/{userId}")
    fun getSubmission(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Path("assignmentId") assignmentId: Int,
        @Path("userId") userId: Int,
        @Query("include[]") include: String = "submission"
    ): Call<Assignment>
}
