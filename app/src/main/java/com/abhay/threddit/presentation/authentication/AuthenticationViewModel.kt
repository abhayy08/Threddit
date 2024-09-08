package com.abhay.threddit.presentation.authentication

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.User
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.common.SnackbarEvent
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.utils.ERROR_TAG
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    private val _uiState = mutableStateOf<AuthScreenState>(AuthScreenState())
    val uiState: State<AuthScreenState> = _uiState


    fun onEvent(event: AuthUiEvents) {
        when (event) {
            is AuthUiEvents.OnEmailChange -> {
                _uiState.value = _uiState.value.copy(email = event.email)
            }

            is AuthUiEvents.OnPasswordChange -> {
                _uiState.value = _uiState.value.copy(password = event.password)
            }

            is AuthUiEvents.OnLogInWithEmail -> {
                onLogInWithEmail(event.openAndPopUp, event.openScreen)
            }

            is AuthUiEvents.OnSignInWithGoogle -> {
                onSignInWithGoogle(event.credential, event.openAndPopUp)
            }

            is AuthUiEvents.OnSignUpWithEmail -> {
                onSignUpWithEmail(event.popUp)
            }

            is AuthUiEvents.OnSignOut -> {
                signOut()
            }

            AuthUiEvents.OnPasswordVisibilityChange -> {
                _uiState.value =
                    _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
            }

            is AuthUiEvents.OnConfirmPasswordChange -> {
                _uiState.value = _uiState.value.copy(confirmPassword = event.confirmPassword)
            }

            AuthUiEvents.OnConfirmPasswordVisibilityChange -> {
                _uiState.value =
                    _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
            }

            AuthUiEvents.OnResendVerificationLink -> {
                resendVerificationlink()
            }
        }
    }

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    suspend fun observeEmailVerification(onEmailVerified: (Any, Any) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        while(!_isEmailVerified.value) {
            currentUser?.reload() // Reloads the user data
            val newVerifiedStatus = currentUser?.isEmailVerified ?: false
            if (newVerifiedStatus != _isEmailVerified.value) {
                _isEmailVerified.value = newVerifiedStatus
                if (_isEmailVerified.value) {
                    onEmailVerified(Graphs.HomeGraph, Graphs.AuthGraph)
                    break
                }
            }
            delay(5000)
        }
    }



        private fun resendVerificationlink() {
            launchCatching {
                accountService.verifyUserAccount(_uiState.value.email, _uiState.value.password)
            }
        }

        fun resetState() {
            _uiState.value = AuthScreenState()
        }

        private fun onLogInWithEmail(openAndPopUp: (Any, Any) -> Unit, openScreen: (Any) -> Unit) {
            launchCatching {

                if (_uiState.value.email.isBlank()) {
                    throw IllegalArgumentException("Email cannot be blank")
                }

                if (_uiState.value.password.isBlank()) {
                    throw IllegalArgumentException("Password cannot be blank")
                }

                accountService.signInWithEmail(uiState.value.email, uiState.value.password)



//                openAndPopUp(Graphs.HomeGraph, Graphs.AuthGraph)
                accountService.verifyUserAccount(_uiState.value.email, _uiState.value.password)

                openScreen(Graphs.AuthGraph.VerificationDialog)


            }
        }

        private fun onSignUpWithEmail(popUp: () -> Unit) {
            launchCatching {

                if (!_uiState.value.email.isValidEmail()) {
                    throw IllegalArgumentException("Invalid Email " + _uiState.value.email)
                }

                if (_uiState.value.password != _uiState.value.confirmPassword) {
                    throw IllegalArgumentException("Passwords do not match")
                }

                accountService.signUpWithEmail(_uiState.value.email, _uiState.value.password)
                signOut()
                popUp()
                resetState()
            }
        }

        private fun onSignInWithGoogle(credential: Credential, openAndPopUp: (Any, Any) -> Unit) {
            launchCatching {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    openAndPopUp(Graphs.HomeGraph, Graphs.AuthGraph)
                } else {
                    Log.d("auth", "Unexpected Credential")// UNEXPECTED CREDENTIAL
                }
            }
        }


        fun signOut() {
            viewModelScope.launch {
                accountService.signOut()
            }
        }

        private fun sendSnackbarMessage(
            message: String,
            snackbarDuration: SnackbarDuration = SnackbarDuration.Short
        ) {
            viewModelScope.launch {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = message,
                        duration = snackbarDuration
                    )
                )
            }
        }

        private fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
            viewModelScope.launch(
                CoroutineExceptionHandler { _, throwable ->

                    when (throwable) {

                        is FirebaseAuthException -> {
                            when (throwable.errorCode) {
                                "ERROR_INVALID_EMAIL" -> sendSnackbarMessage("The email address is badly formatted.")
                                "ERROR_INVALID_CREDENTIAL" -> sendSnackbarMessage("The credentials you provided are incorrect. Please check your email and password.")
                                "ERROR_EMAIL_ALREADY_IN_USE" -> sendSnackbarMessage("This email is already in use.")
                                "ERROR_WRONG_PASSWORD" -> sendSnackbarMessage("Incorrect password.")
                                "ERROR_WEAK_PASSWORD" -> sendSnackbarMessage("Password too weak. It must be at least 6 characters long, include one lowercase letter, one uppercase letter, one digit, and contain no spaces.")
                                "ERROR_USER_NOT_FOUND" -> sendSnackbarMessage("No account found with this email. Please sign up.")
//                            "ERROR_USER_DISABLED" -> sendSnackbarMessage("ERROR_USER_DISABLED")
                                "ERROR_TOO_MANY_REQUESTS" -> sendSnackbarMessage("Too many requests have been made with the email.")
                                else -> sendSnackbarMessage(
                                    throwable.message ?: "An Error Occurred!"
                                )
                            }
                        }

                        is IllegalArgumentException -> {
                            sendSnackbarMessage(throwable.message ?: "Invalid Input")
                        }

                        else -> {
                            sendSnackbarMessage(throwable.message ?: "An unknown error occurred")
                        }
                    }
                    Log.d(ERROR_TAG, throwable.message.orEmpty())
                },
                block = block
            )
    }