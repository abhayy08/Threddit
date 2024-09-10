package com.abhay.threddit.presentation.main

import com.abhay.threddit.R
import com.abhay.threddit.presentation.navigation.routes.Graphs
//
//data class NavBarItem(
//    val title: String,
//    val darkIconType: IconType,
//    val lightIconType: IconType,
//    val route: Any
//)

data class TopLevelRoute<T : Any>(
    val name: String, val route: T,
    val darkIconType: IconType,
    val lightIconType: IconType,
)

// Bottom Screen Routes
val topLevelRoutes = listOf(
    TopLevelRoute(
        name = "Home",
        route = Graphs.Feed.FeedScreen,
        darkIconType = IconType(
            selectedIcon = R.drawable.home_filled,
            unSelectedIcon = R.drawable.home
        ),
        lightIconType = IconType(
            selectedIcon = R.drawable.home_selected,
            unSelectedIcon = R.drawable.home
        ),
    ),
    TopLevelRoute(
        name = "Search",
        route = Graphs.Search.SearchScreen,
        darkIconType = IconType(
            selectedIcon = R.drawable.explore__1_,
            unSelectedIcon = R.drawable.search
        ),
        lightIconType = IconType(
            selectedIcon = R.drawable.explore__2_,
            unSelectedIcon = R.drawable.search
        ),
    ),
    TopLevelRoute(
        name = "Add Post",
        route = Graphs.AddPost.AddPostScreen,
        darkIconType = IconType(
            selectedIcon = R.drawable.add_post,
            unSelectedIcon = R.drawable.add_post_dark
        ),
        lightIconType = IconType(
            selectedIcon = R.drawable.add_post_light,
            unSelectedIcon = R.drawable.add_post_dark
        ),
    ),
    TopLevelRoute(
        name = "Activity",
        route = Graphs.Activity.ActivityScreen,
        darkIconType = IconType(
            selectedIcon = R.drawable.heart__1_,
            unSelectedIcon = R.drawable.heart
        ),
        lightIconType = IconType(
            selectedIcon = R.drawable.heart_selected,
            unSelectedIcon = R.drawable.heart
        ),
    ),
    TopLevelRoute(
        name = "Profile",
        route = Graphs.Profile.ProfileScreen,
        darkIconType = IconType(
            selectedIcon = R.drawable.rectangle_3,
            unSelectedIcon = R.drawable.profile
        ),
        lightIconType = IconType(
            selectedIcon = R.drawable.profile_selected,
            unSelectedIcon = R.drawable.profile
        ),
    ),
)

data class IconType(
    val selectedIcon: Int,
    val unSelectedIcon: Int,
)
