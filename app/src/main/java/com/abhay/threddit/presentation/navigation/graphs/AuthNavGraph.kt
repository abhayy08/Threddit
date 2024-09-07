package com.abhay.threddit.presentation.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.authentication.sign_in.LogInScreen
import com.abhay.threddit.presentation.authentication.sign_up.SignUpScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {

    navigation<Graphs.AuthGraph>(
        startDestination = Graphs.AuthGraph.LogInScreen
    ) {
        composable<Graphs.AuthGraph.LogInScreen> {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)
            LogInScreen(
                viewModel = viewModel,
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
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
                }
            )
        }
    }
}