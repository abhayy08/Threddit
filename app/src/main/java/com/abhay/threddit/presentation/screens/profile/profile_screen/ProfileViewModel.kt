package com.abhay.threddit.presentation.screens.profile.profile_screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService
): ViewModel() {

    val user = firestoreService.getUserFlow()

    fun updateProfileChanges() {

    }

    fun uploadProfileImage(imageUri: Uri) {
        firestoreService.uploadProfileImage(imageUri)
    }

}