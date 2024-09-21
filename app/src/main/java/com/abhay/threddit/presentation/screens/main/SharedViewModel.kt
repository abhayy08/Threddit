package com.abhay.threddit.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val accountService: AccountService,
//    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            accountService.currentUser.collect { firebaseUser ->
                _userId.value = firebaseUser!!.uid
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}