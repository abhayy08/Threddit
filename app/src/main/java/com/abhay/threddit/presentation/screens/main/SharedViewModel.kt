package com.abhay.threddit.presentation.screens.main

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
class SharedViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _thredditUser = MutableStateFlow(ThredditUser())
    val thredditUser: StateFlow<ThredditUser> = _thredditUser.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            firestoreService.getUserFlow().collect { user ->
                _thredditUser.update { user!! }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}