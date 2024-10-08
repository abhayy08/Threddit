package com.abhay.threddit.presentation.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.abhay.threddit.presentation.common.SnackbarController

fun NavGraphBuilder.RootNavGraph(
    navController: NavHostController,
    areUserDetailsAdded: Boolean
) {

    authNavGraph(
        navController = navController,
        areUserDetailsAdded = areUserDetailsAdded
    )

    homeNavGraph(navController = navController)

}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

