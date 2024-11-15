package com.abhay.threddit.domain

data class Post(
    val postId: String,
    val userId: String,
    val username: String,
    val title: String,
    val content: String,
    val date: String,
    val likes: Int,
)
