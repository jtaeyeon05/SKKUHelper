package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.skku_team2.skku_helper.canvas.CanvasRepository
import com.skku_team2.skku_helper.canvas.CombinedAssignmentInfo

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CanvasRepository(application.applicationContext)
    val homepageData: LiveData<List<CombinedAssignmentInfo>> = repository.combinedData
    val isLoading: LiveData<Boolean> = repository.isLoading
    val error: LiveData<String> = repository.error
    fun loadData() {
        // 7. token 없이 호출 (Repository가 알아서 가져옴)
        repository.fetchHomepageData()
    }
}