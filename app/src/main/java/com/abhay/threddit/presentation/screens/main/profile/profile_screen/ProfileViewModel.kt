package com.abhay.threddit.presentation.screens.main.profile.profile_screen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.domain.ThredditUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _userProfile = MutableStateFlow<ThredditUser?>(null)
    val userProfile: StateFlow<ThredditUser?> = _userProfile.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        getPostsByUser()
    }

    fun loadProfile() {
        viewModelScope.launch {
            firestoreService.getUserFlow().collect { user ->
                _userProfile.value = user
            }
        }

    }

    private fun getPostsByUser() {
        viewModelScope.launch {
            firestoreService.getPostsByUserId()
                .catch { exception ->
                    Log.d("ThredditUser", exception.message.toString())
                    _profileState.update { it.copy(posts = emptyList()) }
                }
                .collect { listOfPosts ->
                    _profileState.update { it.copy(posts = listOfPosts) }
                }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        firestoreService.uploadProfileImage(imageUri)
    }

}