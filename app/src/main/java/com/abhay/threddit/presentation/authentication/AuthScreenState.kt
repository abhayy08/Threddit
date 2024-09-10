package com.abhay.threddit.presentation.authentication

data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isConfirmPasswordVisible: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)