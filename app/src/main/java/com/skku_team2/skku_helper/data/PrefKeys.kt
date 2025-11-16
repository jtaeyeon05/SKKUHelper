package com.skku_team2.skku_helper.data


sealed class PrefKeys(val key: String) {
    data object Settings: PrefKeys(
        key = "com.skku_team2.skku_helper.SETTINGS"
    ) {
        const val TOKEN = "com.skku_team2.skku_helper.settings.token"
    }
}
