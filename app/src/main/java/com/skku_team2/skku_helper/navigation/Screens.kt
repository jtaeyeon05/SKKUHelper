package com.skku_team2.skku_helper.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface MainScreen {
    @Serializable
    data object Home: MainScreen

    @Serializable
    data object LogIn: MainScreen
}
