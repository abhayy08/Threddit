package com.abhay.threddit.domain

import android.net.Uri

interface FirestoreService {

    fun uploadProfileImage(imageUri: Uri)

}