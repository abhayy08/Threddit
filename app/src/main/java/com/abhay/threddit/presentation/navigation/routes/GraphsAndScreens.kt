package com.abhay.threddit.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen

@Serializable
sealed class Graphs {

    // Authentication
    @Serializable
    data object AuthGraph : Graphs() {
        @Serializable
        data object LogInScreen : Screen()
        @Serializable
        data object SignUpScreen : Screen()
        @Serializable
        data object VerificationDialog: Screen()
        @Serializable
        data object AddUserDetailsScreen: Screen()
    }

    // Main Graph
    @Serializable
    data object MainNavGraph : Graphs() {

        // Feed Graph
        @Serializable
        data object Feed : Graphs() {
            @Serializable
            data object FeedScreen: Screen()
        }

        // Search Graph
        @Serializable
        data object Search: Graphs() {
            @Serializable
            data object SearchScreen: Screen()
        }

        // Add Post Graph
        @Serializable
        data object AddPost: Graphs() {
            @Serializable
            data object AddPostScreen: Screen()
        }

        // Activity Graph
        @Serializable
        data object Activity: Graphs() {
            @Serializable
            data object ActivityScreen: Screen()
        }

        // Profile Graph
        @Serializable
        data object Profile : Graphs() {
            @Serializable
            data object ProfileScreen: Screen()
        }
    }
}