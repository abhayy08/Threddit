package com.abhay.threddit.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

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
        profilePicUrl: Uri?,
        followers: Int,
        following: Int
    )

    suspend fun getThredditUser(): ThredditUser?

    fun getUserFlow(): Flow<ThredditUser?>
}