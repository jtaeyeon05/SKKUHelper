package com.skku_team2.skku_helper.canvas

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skku_team2.skku_helper.key.PrefKey
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger

data class CombinedAssignmentInfo(
    val assignment: Assignment,
    val courseName: String
)

class CanvasRepository(context: Context) {

    private val client = CanvasClient.api

    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        PrefKey.Settings.key,
        Context.MODE_PRIVATE
    )

    private val _combinedData = MutableLiveData<List<CombinedAssignmentInfo>>()
    val combinedData: LiveData<List<CombinedAssignmentInfo>> = _combinedData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    fun fetchHomepageData() {
        _isLoading.postValue(true)

        val token = sharedPreferences.getString(PrefKey.Settings.TOKEN, null)

        if (token.isNullOrBlank()) {
            _error.postValue("restart the app.")
            _isLoading.postValue(false)
            return
        }

        val apiToken = "Bearer $token"

        client.getCourses(apiToken).enqueue(object : Callback<List<Course>> {
            override fun onResponse(call: Call<List<Course>>, courseResponse: Response<List<Course>>) {
                if (courseResponse.isSuccessful) {
                    val courses = courseResponse.body() ?: emptyList()
                    val courseMap = courses.associateBy({ it.id }, { it.name })

                    fetchAllAssignments(apiToken, courses, courseMap)
                } else {
                    _error.postValue("과목 로딩 실패: ${courseResponse.code()}")
                    _isLoading.postValue(false)
                }
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                _error.postValue(t.message ?: "네트워크 오류 (과목)")
                _isLoading.postValue(false)
            }
        })
    }

    private fun fetchAllAssignments(token: String, courses: List<Course>, courseMap: Map<Int, String>) {
        if (courses.isEmpty()) {
            _combinedData.postValue(emptyList())
            _isLoading.postValue(false)
            return
        }

        val allAssignments = mutableListOf<CombinedAssignmentInfo>()
        val requestCounter = AtomicInteger(courses.size)

        courses.forEach { course ->
            client.getAssignments(token, course.id).enqueue(object : Callback<List<Assignment>> {
                override fun onResponse(call: Call<List<Assignment>>, assignResponse: Response<List<Assignment>>) {
                    if (assignResponse.isSuccessful) {
                        assignResponse.body()?.forEach { assignment ->
                            allAssignments.add(
                                CombinedAssignmentInfo(
                                    assignment = assignment,
                                    courseName = courseMap[assignment.courseId] ?: "알 수 없는 과목"
                                )
                            )
                        }
                    }
                    checkIfDone(requestCounter, allAssignments)
                }

                override fun onFailure(call: Call<List<Assignment>>, t: Throwable) {
                    checkIfDone(requestCounter, allAssignments)
                }
            })
        }
    }

    private fun checkIfDone(counter: AtomicInteger, allAssignments: List<CombinedAssignmentInfo>) {
        if (counter.decrementAndGet() == 0) {
            val sortedList = allAssignments.sortedBy { it.assignment.dueAt }
            _combinedData.postValue(sortedList)
            _isLoading.postValue(false)
        }
    }
}