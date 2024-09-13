package com.abhay.threddit.data.firebase.firestore

import android.net.Uri
import android.util.Log
import com.abhay.threddit.data.firebase.BIO
import com.abhay.threddit.data.firebase.DISPLAY_NAME
import com.abhay.threddit.data.firebase.DOB
import com.abhay.threddit.data.firebase.EMAIL
import com.abhay.threddit.data.firebase.FOLLOWERS
import com.abhay.threddit.data.firebase.FOLLOWING
import com.abhay.threddit.data.firebase.PROFILE_PIC_URL
import com.abhay.threddit.data.firebase.USERID
import com.abhay.threddit.data.firebase.USERNAME
import com.abhay.threddit.data.firebase.USERS
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.domain.ThredditUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.toImmutableList
import javax.inject.Inject
import kotlin.coroutines.resume

class FirestoreServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : FirestoreService {

    override fun uploadProfileImage(imageUri: Uri) {
        val storageRef = storage.reference
        val userId = auth.currentUser!!
        val profileImageRef = storageRef.child("profile_images/${userId.uid}")

        profileImageRef.delete()
            .addOnSuccessListener { uploadImage(imageUri, profileImageRef) }
            .addOnFailureListener { uploadImage(imageUri, profileImageRef) }
    }

    private fun uploadImage(imageUri: Uri, profileImageRef: StorageReference) {
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    saveProfileImageUrlToFirestore(profileImageUrl)
                }
            }
    }



    override suspend fun getUserswithSameUsername(username: String): List<ThredditUser> {
        return suspendCancellableCoroutine { continuation ->
            db.collection(USERS)
                .whereEqualTo(USERNAME, username)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.isEmpty) {
                        val usersWithSameUsernameList =
                            document.toObjects(ThredditUser::class.java).toImmutableList()
                        continuation.resume(usersWithSameUsernameList)
                    }else{
                        continuation.resume(emptyList())
                    }

                }
        }
    }

    override suspend fun createUserInFireStore(
        userId: String,
        name: String,
        username: String,
        email: String,
        dob: String,
        bio: String,
        profilePicUrl: String?,
        followers: Int,
        following: Int
    ) {
        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser!!.uid
        val userData = hashMapOf(
            USERID to userId,
            DISPLAY_NAME to name,
            USERNAME to username,
            EMAIL to email,
            DOB to dob,
            BIO to bio,
            PROFILE_PIC_URL to profilePicUrl,
            FOLLOWING to following,
            FOLLOWERS to followers
        )

        db.collection(USERS).document(userId)
            .set(userData)
            .addOnSuccessListener { Log.d("FirestoreService", "User data successfully stored!") }
            .addOnFailureListener { e -> Log.w("FirestoreService", "Error storing user data", e) }
    }

    private fun saveProfileImageUrlToFirestore(profileImageUrl: String) {
        val userId = auth.currentUser!!.uid

        val userUpdates = mapOf(
            PROFILE_PIC_URL to profileImageUrl
        )

        db.collection(USERS).document(userId)
            .update(userUpdates)
            .addOnSuccessListener {
                Log.d("FirestoreService", "Profile image URL saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Error saving profile image URL to Firestore", e)
            }

    }

}