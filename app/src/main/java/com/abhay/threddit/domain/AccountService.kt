package com.abhay.threddit.domain

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AccountService {

    val currentUser: Flow<FirebaseUser?>
    val currentUserId: String
    fun hasUser(): Boolean
    fun getUserProfile(): User

    fun getFirebaseUser(): FirebaseUser?

    suspend fun updateDisplayName(newDisplayName: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun signUpWithEmail(email: String, password: String)


    suspend fun verifyUserAccount(email: String, password: String)


    suspend fun signOut()
    suspend fun deleteAccount()

}

