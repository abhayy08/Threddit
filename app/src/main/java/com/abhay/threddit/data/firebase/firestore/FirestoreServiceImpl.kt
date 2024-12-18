package com.abhay.threddit.data.firebase.firestore

import android.net.Uri
import android.util.Log
import com.abhay.threddit.data.firebase.BIO
import com.abhay.threddit.data.firebase.DISPLAY_NAME
import com.abhay.threddit.data.firebase.DOB
import com.abhay.threddit.data.firebase.EMAIL
import com.abhay.threddit.data.firebase.FOLLOWERS
import com.abhay.threddit.data.firebase.FOLLOWING
import com.abhay.threddit.data.firebase.IS_USER_REGISTERED
import com.abhay.threddit.data.firebase.POSTS
import com.abhay.threddit.data.firebase.POST_COMMENTS
import com.abhay.threddit.data.firebase.POST_CONTENT
import com.abhay.threddit.data.firebase.POST_DATE
import com.abhay.threddit.data.firebase.POST_ID
import com.abhay.threddit.data.firebase.POST_LIKES
import com.abhay.threddit.data.firebase.POST_TITLE
import com.abhay.threddit.data.firebase.PROFILE_PIC_URL
import com.abhay.threddit.data.firebase.USERID
import com.abhay.threddit.data.firebase.USERNAME
import com.abhay.threddit.data.firebase.USERS
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.domain.Post
import com.abhay.threddit.domain.ThredditUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.toImmutableList
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
            .addOnCompleteListener { uploadImage(imageUri, profileImageRef) }
//            .addOnSuccessListener { uploadImage(imageUri, profileImageRef) }
//            .addOnFailureListener { uploadImage(imageUri, profileImageRef) }
    }

    private fun uploadImage(imageUri: Uri, profileImageRef: StorageReference) {
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    saveProfileImageUrlToFirestore(profileImageUrl)
                }
            }
    }

    override fun getUserFlow(): Flow<ThredditUser?> = callbackFlow {
        val userId = Firebase.auth.currentUser?.uid

        val listenerRegistration = userId?.let {
            db.collection(USERS).document(it)
                .addSnapshotListener { document, e ->
                    if (e != null) {
                        Log.e("FirestoreService", "Error getting user data")
                        close(e)
                        return@addSnapshotListener
                    }

                    if (document != null && document.exists()) {
                        val user = ThredditUser(
                            userId = document.getString(USERID) ?: "",
                            displayName = document.getString(DISPLAY_NAME) ?: "",
                            username = document.getString(USERNAME) ?: "",
                            email = document.getString(EMAIL) ?: "",
                            dob = document.getString(DOB) ?: "",
                            bio = document.getString(BIO) ?: "",
                            profilePicUrl = document.getString(PROFILE_PIC_URL),
                            followers = document.getLong(FOLLOWERS)?.toInt() ?: 0,
                            following = document.getLong(FOLLOWERS)?.toInt() ?: 0,
                            isUserRegistered = document.getBoolean(IS_USER_REGISTERED) ?: false
                        )
                        trySend(user).isSuccess
                        Log.d("FirestoreService", "Emitted user: ${user.displayName}")
                    } else {
                        trySend(null).isSuccess
                        Log.d("ThredditUser", "No user found with the given userId")
                    }
                }
        }


        awaitClose {
            listenerRegistration?.remove()
        }
    }


    override suspend fun getUsersWithSameUsername(username: String): List<ThredditUser> {
        return suspendCancellableCoroutine { continuation ->
            db.collection(USERS)
                .whereEqualTo(USERNAME, username)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.isEmpty) {
                        val usersWithSameUsernameList =
                            document.toObjects(ThredditUser::class.java).toImmutableList()
                        continuation.resume(usersWithSameUsernameList)
                    } else {
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
        profilePicUrl: Uri?,
        followers: Int,
        following: Int
    ) {
        val db = Firebase.firestore
//        val userId = auth.currentUser!!.uid

        val userData = hashMapOf(
            USERID to userId,
            DISPLAY_NAME to name,
            USERNAME to username,
            DOB to dob,
            EMAIL to email,
            BIO to bio,
            PROFILE_PIC_URL to null,
            FOLLOWING to following,
            FOLLOWERS to followers,
            "isUserRegistered" to true
        )

        db.collection(USERS).document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User data successfully stored!")
                profilePicUrl?.let {
                    uploadProfileImage(it)
                }
            }
            .addOnFailureListener { e -> Log.w("FirestoreService", "Error storing user data", e) }
    }

    override suspend fun getThredditUser(): ThredditUser? = suspendCoroutine { continuation ->
        val userId = auth.currentUser?.uid
        var thredditUser: ThredditUser? = null

        userId?.let {
            db.collection(USERS).document(it)
                .get()
                .addOnSuccessListener { document ->

                    if (document != null) {
                        thredditUser = ThredditUser(
                            userId = document.getString(USERID) ?: "",
                            displayName = document.getString(DISPLAY_NAME) ?: "",
                            username = document.getString(USERNAME) ?: "",
                            email = document.getString(EMAIL) ?: "",
                            dob = document.getString(DOB) ?: "",
                            isUserRegistered = document.getBoolean(IS_USER_REGISTERED) ?: false,
                        )
                        Log.d("ThredditUser", thredditUser.toString())
                    }
                    continuation.resume(thredditUser)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } ?: continuation.resume(null)
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

    override fun addPost(
        username: String,
        title: String,
        content: String,
        date: String,
        onResult: (Boolean) -> Unit
    ) {
        val postId = db.collection(POSTS).document().id
        val userId = auth.currentUser!!.uid
        val post = hashMapOf(
            POST_ID to postId,
            USERID to userId,
            USERNAME to username,
            POST_TITLE to title,
            POST_CONTENT to content,
            POST_DATE to date,
            POST_LIKES to 0,
            POST_COMMENTS to emptyList<String>()
        )

        db.collection(POSTS).document(postId)
            .set(post)
            .addOnSuccessListener {
                Log.d(
                    "FirestoreService",
                    "Post added Successully! Post content: ${post[POST_CONTENT]}"
                )
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
                Log.d("FirestoreService", "cannot add post" + it.message)
            }
    }

    override fun getPostsByUserId(): Flow<List<Post>> = callbackFlow {
        val userId = auth.currentUser!!.uid

        val listener = db.collection(POSTS)
            .whereEqualTo(USERID, userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // Close the flow if there's an error
                    return@addSnapshotListener
                }

                var postsList = snapshot?.documents?.mapNotNull {
                    Post(
                        postId = it.getString(POST_ID) ?: "",
                        userId = it.getString(USERID) ?: "",
                        title = it.getString(POST_TITLE) ?: "",
                        content = it.getString(POST_CONTENT) ?: "",
                        date = it.getString(POST_DATE) ?: "",
                        likes = it.getLong(POST_LIKES)?.toInt() ?: 0,
                        username = it.getString(USERNAME) ?: "",
                    )
                } ?: emptyList()

                postsList = postsList.sortedByDescending { it.date }

                trySend(postsList).isSuccess // Emit the updated list of posts
            }

        awaitClose { listener.remove() } // Remove the listener when the flow is closed
    }


}
