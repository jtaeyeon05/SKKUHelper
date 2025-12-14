package com.skku_team2.skku_helper.key

/**
 * SharedPreferences로 정보를 저장할 때 사용하는 Key를 정의한 객체
 */

sealed class PrefKey(val key: String) {
    data object Settings: PrefKey(
        key = "com.skku_team2.skku_helper.SETTINGS"
    ) {
        const val TOKEN = "com.skku_team2.skku_helper.settings.token"
    }
}