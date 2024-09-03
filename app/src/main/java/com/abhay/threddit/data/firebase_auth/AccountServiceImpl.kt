package com.abhay.threddit.data.firebase_auth

import com.abhay.threddit.domain.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class AccountServiceImpl: AccountService {

    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    override suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

}