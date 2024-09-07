package com.abhay.threddit.presentation.authentication

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.presentation.SIGN_IN_SCREEN
import com.abhay.threddit.presentation.SIGN_UP_SCREEN
import com.abhay.threddit.presentation.THREDDIT_MAIN_SCREEN
import com.abhay.threddit.presentation.navigation.changes.Graphs
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {


    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    openAndPopUp(Graphs.HomeGraph.route, Graphs.AuthGraph.route)
                } else {
                    Log.d("auth", "Unexpected Credential")// UNEXPECTED CREDENTIAL
                }

            } catch (e: Exception) {
                Log.d("auth", e.message.orEmpty()) // Explain use why
            }
        }
    }

    fun onSignUpWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                    openAndPopUp(Graphs.HomeGraph.route, Graphs.AuthGraph.route)
                } else {
                    Log.d("auth", "Unexpected Credential")// UNEXPECTED CREDENTIAL
                }

            } catch (e: Exception) {
                Log.d("auth", e.message.orEmpty()) // Explain user why
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}