package com.skku_team2.skku_helper.key


sealed class PrefKey(val key: String) {
    data object Settings: PrefKey(
        key = "com.skku_team2.skku_helper.SETTINGS"
    ) {
        const val TOKEN = "com.skku_team2.skku_helper.settings.token"
    }
}
