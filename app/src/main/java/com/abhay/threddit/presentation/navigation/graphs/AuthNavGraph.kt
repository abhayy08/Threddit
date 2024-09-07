package com.abhay.threddit.presentation.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.authentication.sign_in.LogInScreen
import com.abhay.threddit.presentation.authentication.sign_up.SignUpScreen
import com.abhay.threddit.presentation.navigation.changes.Graphs


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Graphs.AuthGraph.LogInScreen.route,
        route = Graphs.AuthGraph.route
    ) {
        composable(Graphs.AuthGraph.LogInScreen.route) {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)
            LogInScreen(
                viewModel = viewModel,
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                }
            )
        }
        composable(Graphs.AuthGraph.SignUpScreen.route) {
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