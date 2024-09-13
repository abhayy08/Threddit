package com.abhay.threddit.data.firebase.auth

import android.util.Log
import com.abhay.threddit.data.firebase.BIO
import com.abhay.threddit.data.firebase.DISPLAY_NAME
import com.abhay.threddit.data.firebase.EMAIL
import com.abhay.threddit.data.firebase.PROFILE_PIC_URL
import com.abhay.threddit.data.firebase.USERNAME
import com.abhay.threddit.data.firebase.USERS
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.ThredditUser
import com.abhay.threddit.domain.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    // Sign in with Google using the ID Token
    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    // Sign in with email and password
    override suspend fun signInWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    // Sign up with email and password
    override suspend fun signUpWithEmail(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    // Send email verification
    override suspend fun verifyUserAccount(email: String, password: String) {
        Firebase.auth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun createUserInFireStore(
        name: String,
        username: String,
        email: String,
        bio: String
    ) {
        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser!!.uid
        val userData = hashMapOf(
            DISPLAY_NAME to name,
            USERNAME to username,
            PROFILE_PIC_URL to null,
            EMAIL to email,
            BIO to bio
        )

        db.collection(USERS).document(userId)
            .set(userData)
            .addOnSuccessListener { Log.d("FirestoreService", "User data successfully stored!") }
            .addOnFailureListener { e -> Log.w("FirestoreService", "Error storing user data", e) }

    }

    override fun getUserFlow(): Flow<ThredditUser> = callbackFlow {
        val userId = Firebase.auth.currentUser!!.uid

        val listenerRegistration = Firebase.firestore.collection(USERS).document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirestoreService", "Error getting user data")
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val user = snapshot.toObject(ThredditUser::class.java)
                    if (user != null) {
                        trySend(user).isSuccess
                    }
                    Log.d("ThredditUser", user?.name ?: "Something not working")
                }
            }
        awaitClose { listenerRegistration.remove() }
    }


    // Observe the current Firebase user with Flow
    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    // Get the current user's ID
    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    // Check if there is a current user signed in
    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    // Get the profile of the current user
    override fun getUserProfile(): User {
        return Firebase.auth.currentUser.toThredditUser()
    }

    // Update the display name of the current user
    override suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }
        Firebase.auth.currentUser?.updateProfile(profileUpdates)?.await()
    }

    // Sign out the current user
    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    // Delete the current user account
    override suspend fun deleteAccount() {
        Firebase.auth.currentUser?.delete()?.await()
    }

    // Helper function to convert FirebaseUser to ThredditUser
    private fun FirebaseUser?.toThredditUser(): User {
        return if (this == null) {
            User()
        } else {
            User(
                id = this.uid,
                email = (this.email ?: false).toString(),
                provider = this.providerId,
                displayName = this.displayName.orEmpty(),
                isEmailVerified = this.isEmailVerified
            )
        }
    }

}
