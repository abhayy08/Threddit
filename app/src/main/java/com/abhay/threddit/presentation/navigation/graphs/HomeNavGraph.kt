package com.abhay.threddit.presentation.navigation.graphs

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.presentation.screens.main.activity.ActivityScreen
import com.abhay.threddit.presentation.screens.main.add_post.AddPostScreen
import com.abhay.threddit.presentation.screens.main.feed.FeedScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.presentation.screens.main.add_post.AddPostViewModel
import com.abhay.threddit.presentation.screens.main.profile.profile_screen.ProfileScreen
import com.abhay.threddit.presentation.screens.main.search.SearchScreen
import com.abhay.threddit.utils.navigateAndPopUp


fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation<Graphs.MainNavGraph>(
        startDestination = Graphs.MainNavGraph.Feed.FeedScreen,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None }
    ) {
        composable<Graphs.MainNavGraph.Feed.FeedScreen> {
            FeedScreen()
        }

        composable<Graphs.MainNavGraph.Search.SearchScreen> {
            SearchScreen()
        }

        composable<Graphs.MainNavGraph.AddPost.AddPostScreen> {
            val viewModel = hiltViewModel<AddPostViewModel>()
            AddPostScreen(
                state = viewModel.addpoststate.value,
                onEvent = viewModel::onEvent,
                openAndPopUp = { route, popUp ->
                    navController.navigateAndPopUp(route, popUp)
                }
            )
        }

        composable<Graphs.MainNavGraph.Activity.ActivityScreen> {
            ActivityScreen()
        }

        composable<Graphs.MainNavGraph.Profile.ProfileScreen> {
            ProfileScreen()
        }

    }
}
