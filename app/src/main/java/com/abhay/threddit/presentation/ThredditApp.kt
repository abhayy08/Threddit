package com.abhay.threddit.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.abhay.threddit.presentation.common.ObserveAsEvents
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.presentation.navigation.graphs.RootNavGraph
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

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

            val snackBarHostState = remember {
                SnackbarHostState()
            }

            val scope = rememberCoroutineScope()
            
            ObserveAsEvents(flow = SnackbarController.event, snackBarHostState) { event ->
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action?.name,
                        duration = event.duration
                    )

                    if(result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                },
                topBar = {ThredditTopAppBar()}
            ) { innerPadding ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThredditTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { /*TODO*/ }
    )
}


