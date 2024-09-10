package com.abhay.threddit.data.firebase_auth

import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.User
import com.google.firebase.auth.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    // Sign in with Google using the ID Token
    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
    }

    // Sign in with email and password
    override suspend fun signInWithEmail(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
    }

    // Sign up with email and password
    override suspend fun signUpWithEmail(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
    }

    // Send email verification
    override suspend fun verifyUserAccount(email: String, password: String) {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.await()
    }

    // Observe the current Firebase user with Flow
    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }
            FirebaseAuth.getInstance().addAuthStateListener(listener)
            awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
        }

    // Get the current user's ID
    override val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    // Check if there is a current user signed in
    override fun hasUser(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    // Get the profile of the current user
    override fun getUserProfile(): User {
        return FirebaseAuth.getInstance().currentUser.toThredditUser()
    }

    // Update the display name of the current user
    override suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }
        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)?.await()
    }

    // Sign out the current user
    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    // Delete the current user account
    override suspend fun deleteAccount() {
        FirebaseAuth.getInstance().currentUser?.delete()?.await()
    }

    // Helper function to convert FirebaseUser to ThredditUser
    private fun FirebaseUser?.toThredditUser(): User {
        return if (this == null) {
            User()
        } else {
            User(
                id = this.uid,
                email = this.email.orEmpty(),
                provider = this.providerId,
                displayName = this.displayName.orEmpty(),
                isVerified = this.isEmailVerified
            )
        }
    }

}
