package com.abhay.threddit.utils

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.popUp() {
    popBackStack()
}

fun NavHostController.navigate(route: Any) {
    navigate(route) { launchSingleTop = true }
}

fun NavHostController.navigateAndPopUp(route: Any, popUp: Any) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}

fun NavHostController.navigateWithStateAndPopToStart(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {

            // Save backstack state. This will ensure restoration of
            // nested navigation screen when the user comes back to
            // the destination.
            saveState = true
        }
        //prevent duplicate destinations when navigation is clicked on
        // multiple times
        launchSingleTop = true

        // restore state if previously saved
        restoreState = true

    }
}

fun NavHostController.clearAndNavigate(route: Any) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}