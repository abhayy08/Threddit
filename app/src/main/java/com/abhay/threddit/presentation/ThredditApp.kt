package com.abhay.threddit.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhay.threddit.presentation.authentication.sign_in.SignInScreen
import com.abhay.threddit.presentation.authentication.sign_up.SignUpScreen
import com.abhay.threddit.presentation.navigation.thredditGraph
import com.abhay.threddit.ui.theme.ThredditTheme

@Composable
fun ThredditApp() {
    ThredditTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val appState = rememberAppState()

            Scaffold { innerPadding ->
                NavHost(
                    navController = appState.navController,
                    startDestination = THREDDIT_MAIN_SCREEN,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    thredditGraph(appState)
                }

            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        ThredditAppState(navController)
    }

