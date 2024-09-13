package com.abhay.threddit.presentation.screens.authentication

data class AuthScreenState(
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val bio: String = "",
    val isConfirmPasswordVisible: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,

    val checkingUsernameUniqueness: Boolean = false,

    val usernameError: Boolean = false,
    val nameError: Boolean = false,
    val dobError: Boolean = false,
    val dob: String = "",
)