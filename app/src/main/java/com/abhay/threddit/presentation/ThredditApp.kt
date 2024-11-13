package com.abhay.threddit.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abhay.threddit.R
import com.abhay.threddit.presentation.common.ObserveAsEvents
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.navigation.graphs.RootNavGraph
import com.abhay.threddit.ui.theme.ThredditInlineFont
import com.abhay.threddit.ui.theme.ThredditTheme
import com.abhay.threddit.utils.navigateWithStateAndPopToStart
import kotlinx.coroutines.launch

@Composable
fun ThredditApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    ThredditTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val isUserLoggedIn = viewModel.isUserLoggedIn()
            val startDest = viewModel.getStartDestination()

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
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                },
//                topBar = {
//                    val navBackStackEntry by navController.currentBackStackEntryAsState()
//                    val currentDestination = navBackStackEntry?.destination
//                    ThredditTopAppBar(currentDestination = currentDestination)
//                },
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    ThredditNavigationBar(
                        modifier = Modifier.height(70.dp),
                        containerColor = Color.Transparent,
                        currentDestination = currentDestination,
                        onClick = { route ->
                            navController.navigateWithStateAndPopToStart(route)
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(WindowInsets.ime.asPaddingValues())
                ) {
                    RootNavGraph(
                        navController = navController,
                        areUserDetailsAdded = true
                    )
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
    val isVisible = topLevelRoutes.any { topLevelRoute ->
        currentDestination?.hierarchy?.any {
            it.hasRoute(topLevelRoute.route::class)
        } == true
    }

    if (isVisible) {
        NavigationBar(
            modifier = modifier,
            containerColor = containerColor
        ) {
            topLevelRoutes.forEach { topLevelRoute ->

                val iconType =
                    if (isDarkTheme) topLevelRoute.lightIconType else topLevelRoute.darkIconType
                val isSelected =
                    currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true

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
fun ThredditTopAppBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?
) {

    val isVisible = topLevelRoutes.any { topLevelRoute ->
        currentDestination?.hierarchy?.any {
            it.hasRoute(topLevelRoute.route::class)
        } == true
    }
    if (isVisible) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = "Threddit",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = ThredditInlineFont)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "menu"
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppPrev() {
    ThredditApp()

}



