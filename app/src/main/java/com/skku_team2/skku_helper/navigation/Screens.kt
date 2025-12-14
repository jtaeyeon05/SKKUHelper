package com.skku_team2.skku_helper.navigation

import kotlinx.serialization.Serializable

/**
 * 액티비티 내 Navigation을 정의하는 실드 인터페이스 모음
 */

/**
 * StartActivity에서 사용되는 화면 그룹
 */
@Serializable
sealed interface StartScreen {
    @Serializable
    data object Start: StartScreen

    @Serializable
    data object LogIn: StartScreen
}

/**
 * MainActivity에서 사용되는 화면 그룹
 */
@Serializable
sealed interface MainScreen {
    @Serializable
    data object Calendar: MainScreen

    @Serializable
    data object Home: MainScreen

    @Serializable
    data object Account: MainScreen
}

/**
 * AssignmentActivity에서 사용되는 화면 그룹
 */
@Serializable
sealed interface AssignmentScreen {

    @Serializable
    data object Information: MainScreen

    @Serializable
    data object Detail: MainScreen
}
