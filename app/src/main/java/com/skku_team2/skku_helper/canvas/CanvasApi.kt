package com.skku_team2.skku_helper.canvas

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface CanvasApi {
    @GET("api/v1/courses?enrollment_state=active")
    fun getCourses(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 150
    ): Call<List<Course>>
}
