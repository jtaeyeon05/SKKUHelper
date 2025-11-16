package com.skku_team2.skku_helper.canvas

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CanvasClient {
    private const val BASE_URL = "https://canvas.skku.edu/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: CanvasApi by lazy {
        retrofit.create(CanvasApi::class.java)
    }
}
