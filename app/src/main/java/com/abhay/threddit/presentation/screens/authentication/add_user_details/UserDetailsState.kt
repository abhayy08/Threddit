package com.abhay.threddit.presentation.screens.authentication.add_user_details

import android.net.Uri

data class UserDetailsState(
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val dob: String = "",
    val isLoading: Boolean = false,
    val checkingUsernameUniqueness: Boolean = false,
    val usernameError: Boolean = false,
    val nameError: Boolean = false,
    val dobError: Boolean = false,
    val profilePicUri: Uri? = null

)