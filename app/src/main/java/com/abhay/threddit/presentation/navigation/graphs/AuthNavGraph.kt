package com.abhay.threddit.presentation.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.abhay.threddit.presentation.screens.authentication.add_user_details.AddUserDetailsScreen
import com.abhay.threddit.presentation.screens.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.screens.authentication.VerificationScreen
import com.abhay.threddit.presentation.screens.authentication.log_in.LogInScreen
import com.abhay.threddit.presentation.screens.authentication.sign_up.SignUpScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.utils.navigate
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.utils.popUp


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    areUserDetailsAdded: Boolean
) {

    navigation<Graphs.AuthGraph>(
        startDestination = Graphs.AuthGraph.LogInScreen,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
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
                openScreen = { route ->
                    navController.navigate(route)
                },
                popUp = {
                    navController.popUp()
                }
            )
        }

        composable<Graphs.AuthGraph.AddUserDetailsScreen> {
            val viewModel =
                it.sharedViewModel<AuthenticationViewModel>(navController = navController)

            AddUserDetailsScreen(
                state = viewModel.userDetailState.value,
                onOpenAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                },
                popUp = {
                    navController.popUp()
                },
                onSignOut = { viewModel.signOut() },
                onEvent = viewModel::onEvent
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
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                },
                popUp = {
                    navController.popUp()
                }
            )
        }
    }
}