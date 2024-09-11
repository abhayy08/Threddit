package com.abhay.threddit.presentation.navigation.graphs

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.abhay.threddit.presentation.activity.ActivityScreen
import com.abhay.threddit.presentation.add_post.AddPostScreen
import com.abhay.threddit.presentation.feed.FeedScreen
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.presentation.profile_screen.ProfileScreen
import com.abhay.threddit.presentation.search.SearchScreen


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
            AddPostScreen()
        }

        composable<Graphs.MainNavGraph.Activity.ActivityScreen> {
            ActivityScreen()
        }

        composable<Graphs.MainNavGraph.Profile.ProfileScreen> {
            ProfileScreen()
        }

    }
}
