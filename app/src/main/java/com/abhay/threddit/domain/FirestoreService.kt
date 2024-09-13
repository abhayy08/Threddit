package com.abhay.threddit.domain

import android.net.Uri

interface FirestoreService {

    fun uploadProfileImage(imageUri: Uri)

    suspend fun getUserswithSameUsername(username: String): List<ThredditUser>

    suspend fun createUserInFireStore(
        userId: String,
        name: String,
        username: String,
        email: String,
        dob: String,
        bio: String,
        profilePicUrl: String?,
        followers: Int,
        following: Int
    )
}