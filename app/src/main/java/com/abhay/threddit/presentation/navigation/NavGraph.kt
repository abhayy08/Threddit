package com.abhay.threddit.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abhay.threddit.presentation.MainScreen
import com.abhay.threddit.presentation.SIGN_IN_SCREEN
import com.abhay.threddit.presentation.SIGN_UP_SCREEN
import com.abhay.threddit.presentation.THREDDIT_MAIN_SCREEN
import com.abhay.threddit.presentation.ThredditAppState
import com.abhay.threddit.presentation.authentication.sign_in.SignInScreen
import com.abhay.threddit.presentation.authentication.sign_up.SignUpScreen

fun NavGraphBuilder.thredditGraph(appState: ThredditAppState) {

    composable(THREDDIT_MAIN_SCREEN) {
        MainScreen(
            openScreen = { route ->
                appState.navigate(route)
            }
        )
    }

    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openAndPopUp = {route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(
            openAndPopUp = {route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }
}