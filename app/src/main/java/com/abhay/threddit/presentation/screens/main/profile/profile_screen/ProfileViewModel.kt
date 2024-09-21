package com.abhay.threddit.presentation.screens.main.profile.profile_screen

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.domain.ThredditUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreService: FirestoreService
): ViewModel() {

    private val _userProfile = MutableStateFlow<ThredditUser?>(null)
    val userProfile: StateFlow<ThredditUser?> = _userProfile.asStateFlow()


    fun loadProfile(){
        viewModelScope.launch {
            firestoreService.getUserFlow().collect { user ->
                _userProfile.value = user
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        firestoreService.uploadProfileImage(imageUri)
    }

}