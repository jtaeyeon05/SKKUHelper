package com.skku_team2.skku_helper.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface StartScreen {
    @Serializable
    data object Home: StartScreen

    @Serializable
    data object LogIn: StartScreen
}
