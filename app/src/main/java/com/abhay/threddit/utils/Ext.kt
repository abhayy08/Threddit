package com.abhay.threddit.utils

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

fun NavHostController.clearAndNavigate(route: Any) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}