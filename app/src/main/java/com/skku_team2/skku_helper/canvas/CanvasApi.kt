package com.skku_team2.skku_helper.canvas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface CanvasApi {
    @GET("api/v1/courses?enrollment_state=active")
    fun getCourses(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 150
    ): Call<List<Course>>

    @GET("api/v1/courses/{courseId}")
    fun getCourse(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Call<Course>

    @GET("api/v1/courses/{courseId}/assignments")
    fun getAssignments(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Query("include[]") include: String = "submission"
    ): Call<List<Assignment>>

    @GET("api/v1/courses/{courseId}/assignments/{assignmentId}")
    fun getAssignment(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int,
        @Path("assignmentId") assignmentId: Int
    ): Call<Assignment>
}
