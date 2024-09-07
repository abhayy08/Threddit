package com.abhay.threddit.presentation.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.presentation.MainScreen
import com.abhay.threddit.presentation.navigation.changes.Graphs


fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Graphs.HomeGraph.HomeScreen.route,
        route = Graphs.HomeGraph.route
    ) {
        composable(Graphs.HomeGraph.HomeScreen.route) {
            MainScreen(
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                }
            )

        }
    }
}