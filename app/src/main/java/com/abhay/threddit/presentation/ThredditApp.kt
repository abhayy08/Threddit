package com.abhay.threddit.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.presentation.navigation.graphs.RootNavGraph
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ThredditApp() {
    ThredditTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val isUserLoggedIn = Firebase.auth.currentUser != null
            val startDest =
                if (isUserLoggedIn) Graphs.HomeGraph else Graphs.AuthGraph

            Scaffold { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    RootNavGraph(navController = navController)
                }

            }
        }
    }
}


