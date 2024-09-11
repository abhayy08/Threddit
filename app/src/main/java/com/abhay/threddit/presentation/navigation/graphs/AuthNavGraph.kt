package com.abhay.threddit.presentation.navigation.graphs

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.authentication.VerificationScreen
import com.abhay.threddit.presentation.authentication.log_in.LogInScreen
import com.abhay.threddit.presentation.authentication.sign_up.SignUpScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.utils.popUp


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {

    navigation<Graphs.AuthGraph>(
        startDestination = Graphs.AuthGraph.LogInScreen,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None }
    ) {
        composable<Graphs.AuthGraph.LogInScreen> {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)
            LogInScreen(
                viewModel = viewModel,
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                },
                openScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable<Graphs.AuthGraph.SignUpScreen> {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)
            SignUpScreen(
                viewModel = viewModel,
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                },
                popUp = {
                    navController.popUp()
                }
            )
        }

        dialog<Graphs.AuthGraph.VerificationDialog>(
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)

            VerificationScreen(
                viewModel = viewModel,
                openAndPopUp = {route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                }
            )
        }
    }
}