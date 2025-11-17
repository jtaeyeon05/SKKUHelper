package com.skku_team2.skku_helper

import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.canvas.Course
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


class CanvasUnitTest {
    interface CanvasApiTest {
        @GET("api/v1/courses")
        fun getCourses(
            @Header("Authorization") token: String,
            @Query("enrollment_state") state: String = "active",
            @Query("per_page") perPage: Int = 100
        ): Call<List<Course>>

        @GET("api/v1/courses/{courseId}/assignments")
        fun getAssignments(
            @Header("Authorization") token: String,
            @Path("courseId") courseId: Int
        ): Call<List<Assignment>>

        @GET("api/v1/courses/{courseId}/assignments/{assignmentId}")
        fun getAssignment(
            @Header("Authorization") token: String,
            @Path("courseId") courseId: Int,
            @Path("assignmentId") assignmentId: Int
        ): Call<Assignment>
    }

    @Test
    fun testCanvasApi() {
        println("과제 리스트 테스트 시작")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://canvas.skku.edu/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CanvasApiTest::class.java)

        val myToken = "Bearer ${Secret.CANVAS_KEY}"

        try {
            println(">>> 1. 수강 중인 과목 목록을 요청")
            val courseResponse = api.getCourses(myToken).execute()

            if (courseResponse.isSuccessful) {
                val courses = courseResponse.body() ?: emptyList()
                println(">>> 성공! 총 ${courses.size}개의 과목\n")

                courses.forEach { course ->
                    println("--------------------------------------------------")
                    println("[과목] ${course.name} (ID: ${course.id})")

                    val assignResponse = api.getAssignments(myToken, course.id).execute()

                    if (assignResponse.isSuccessful) {
                        val assignments = assignResponse.body() ?: emptyList()
                        if (assignments.isEmpty()) {
                            println("   -> 과제 없음")
                        } else {
                            assignments.forEach { assignment ->
                                println("   - 과제명: ${assignment.name} (ID: ${assignment.id})")
                                println("     마감일: ${assignment.dueAt ?: "없음"}")
                            }
                        }
                    } else {
                        println("   -> 과제 목록 가져오기 실패")
                    }
                }

            } else {
                println(">>> 과목 목록 가져오기 실패: ${courseResponse.code()}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            fail("에러 발생: ${e.message}")
        }

        println("과제 리스트 테스트 종료")

        println("단일 과제 테스트 시작")
        try {
            val courseResponse = api.getCourses(myToken).execute()

            if (courseResponse.isSuccessful) {
                val course = (courseResponse.body() ?: emptyList())[0]
                println("[과목] ${course.name} (ID: ${course.id})")

                val assignResponse = api.getAssignments(myToken, course.id).execute()
                if (assignResponse.isSuccessful) {
                    val assignment = (assignResponse.body() ?: emptyList())[0]
                    println("   - 과제명: ${assignment.name} (ID: ${assignment.id})")
                    println("     마감일: ${assignment.dueAt ?: "없음"}")

                    val singleAssignResponse = api.getAssignment(myToken, course.id, assignment.id).execute()
                    if (singleAssignResponse.isSuccessful) {
                        val singleAssignment = singleAssignResponse.body()
                        println("   - 과제명: ${singleAssignment?.name} (ID: ${singleAssignment?.id})")
                        println("     마감일: ${singleAssignment?.dueAt ?: "없음"}")
                    } else {
                        println("   -> 단일 과제 가져오기 실패")
                    }

                } else {
                    println("   -> 과제 목록 가져오기 실패")
                }

            } else {
                println(">>> 과목 목록 가져오기 실패: ${courseResponse.code()}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            fail("에러 발생: ${e.message}")
        }
        println("단일 과제 테스트 종료")
    }


}
