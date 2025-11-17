package com.skku_team2.skku_helper.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.skku_team2.skku_helper.key.IntentKey


class MainViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    val token = savedStateHandle.get<String>(IntentKey.EXTRA_TOKEN) ?: ""
}
