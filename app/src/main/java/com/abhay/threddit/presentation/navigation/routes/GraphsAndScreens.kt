package com.abhay.threddit.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen

@Serializable
sealed class Graphs {

    @Serializable
    data object HomeGraph : Graphs() {
        @Serializable
        data object HomeScreen : Screen()
    }

    @Serializable
    data object AuthGraph : Graphs() {
        @Serializable
        data object LogInScreen : Screen()
        @Serializable
        data object SignUpScreen : Screen()
        @Serializable
        data object VerificationDialog: Screen()

    }
}