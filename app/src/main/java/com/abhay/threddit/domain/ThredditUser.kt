package com.abhay.threddit.domain

data class ThredditUser(
    val userId: String? = null ,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val dob: String = "",
    val bio: String = "",
    val profilePicUrl: String? = null,
    val followers: Int = 0,
    val following: Int = 0,
)