package com.abhay.threddit.data.firebase.firestore

import android.net.Uri
import android.util.Log
import com.abhay.threddit.data.firebase.PROFILE_PIC_URL
import com.abhay.threddit.data.firebase.USERS
import com.abhay.threddit.domain.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

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