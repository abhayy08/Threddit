package com.abhay.threddit.presentation.screens.main.profile.profile_screen

import com.abhay.threddit.domain.Post

data class ProfileState(
    val posts: List<Post> = emptyList(),
)
