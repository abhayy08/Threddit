package com.abhay.threddit.utils

import androidx.navigation.NavHostController

fun NavHostController.popUp() {
    popBackStack()
}

fun NavHostController.navigate(route: String) {
    navigate(route) { launchSingleTop = true }
}

fun NavHostController.navigateAndPopUp(route: String, popUp: String) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}

fun NavHostController.clearAndNavigate(route: String) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}