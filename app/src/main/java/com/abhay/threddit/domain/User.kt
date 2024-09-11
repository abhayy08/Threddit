package com.abhay.threddit.domain

data class User(
    val id: String = "",
    val email: String = "",
    val isEmailVerified: Boolean = false,
    val provider: String = "",
    val displayName: String = ""
)