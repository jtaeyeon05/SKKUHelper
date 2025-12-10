package com.skku_team2.skku_helper.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface StartScreen {
    @Serializable
    data object Start: StartScreen

    @Serializable
    data object LogIn: StartScreen
}


@Serializable
sealed interface MainScreen {
    @Serializable
    data object Calendar: MainScreen

    @Serializable
    data object Home: MainScreen

    @Serializable
    data object Account: MainScreen
}

@Serializable
sealed interface AssignmentScreen {

    @Serializable
    data object Information: MainScreen

    @Serializable
    data object Detail: MainScreen
}
