package com.abhay.threddit.presentation.main

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhay.threddit.presentation.common.ObserveAsEvents
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.navigation.graphs.RootNavGraph
import com.abhay.threddit.presentation.navigation.routes.Graphs
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
//            val isUserLoggedIn = true
            val startDest =
                if (isUserLoggedIn && Firebase.auth.currentUser?.isEmailVerified == true) Graphs.Feed else Graphs.AuthGraph

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

                    if (result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                },
                topBar = { ThredditTopAppBar(modifier = Modifier.height(50.dp)) },
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    ThredditNavigationBar(
                        modifier = Modifier.height(70.dp),
                        containerColor = Color.Transparent,
                        currentDestination = currentDestination,
                        onClick = { route ->
                            navController.navigate(route)
                        }
                    )
                }
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

@Composable
fun ThredditNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    currentDestination: NavDestination?,
    onClick: (Any) -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isVisible = topLevelRoutes.any { topLevelRoute -> currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true }

    if(isVisible){
        NavigationBar(
            modifier = modifier,
            containerColor = containerColor
        ) {
            topLevelRoutes.forEach { topLevelRoute ->

                val iconType = if (isDarkTheme) topLevelRoute.lightIconType else topLevelRoute.darkIconType
                val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onClick(topLevelRoute.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = if (isSelected) iconType.selectedIcon else iconType.unSelectedIcon),
                            contentDescription = ""
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThredditTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = { /*TODO*/ },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppPrev() {
    ThredditTheme {
        ThredditApp()
    }
}



