package com.skku_team2.skku_helper.canvas

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Canvas LMS API와 통신하기 위한 Retrofit 클라이언트 객체
 */

object CanvasClient {
    // 성균관대학교 아이캠퍼스 기본 URL
    private const val BASE_URL = "https://canvas.skku.edu/"
    // Retrofit 객체
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // CanvasApi 인터페이스를 구현한 Retrofit 서비스 객체
    val api: CanvasApi by lazy {
        retrofit.create(CanvasApi::class.java)
    }
}
