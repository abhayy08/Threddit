package com.abhay.threddit.presentation.navigation.changes

sealed class Screen(val route: String)

sealed class Graphs(val route: String) {

    data object HomeGraph: Graphs(route = "home_graph") {
        data object HomeScreen : Screen("home_screen")
    }

    data object AuthGraph: Graphs(route = "auth_graph") {
        data object LogInScreen : Screen("log_in_screen")
        data object SignUpScreen : Screen("sign_up_screen")
    }

}

