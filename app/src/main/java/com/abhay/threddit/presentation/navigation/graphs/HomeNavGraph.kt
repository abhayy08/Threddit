package com.abhay.threddit.presentation.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.utils.navigateAndPopUp
import com.abhay.threddit.presentation.MainScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs


fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation<Graphs.HomeGraph>(
        startDestination = Graphs.HomeGraph.HomeScreen
    ) {
        composable<Graphs.HomeGraph.HomeScreen> {
            MainScreen(
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                }
            )

        }
    }
}