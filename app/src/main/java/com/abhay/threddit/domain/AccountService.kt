package com.abhay.threddit.domain

import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

interface AccountService {

    suspend fun signInWithGoogle(idToken: String)

    suspend fun linkAccountWithGoogle(idToken: String)

    suspend fun signOut()

}