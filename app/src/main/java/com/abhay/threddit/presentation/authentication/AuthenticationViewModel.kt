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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = mutableStateOf(AuthScreenState())
    val uiState: State<AuthScreenState> = _uiState

    private val _isEmailVerified = MutableStateFlow(false)


    // Handle UI Events
    fun onEvent(event: AuthUiEvents) {
        when (event) {
            is AuthUiEvents.OnEmailChange -> updateEmail(event.email)
            is AuthUiEvents.OnPasswordChange -> updatePassword(event.password)
            is AuthUiEvents.OnConfirmPasswordChange -> updateConfirmPassword(event.confirmPassword)
            AuthUiEvents.OnPasswordVisibilityChange -> togglePasswordVisibility()
            AuthUiEvents.OnConfirmPasswordVisibilityChange -> toggleConfirmPasswordVisibility()
            is AuthUiEvents.OnLogInWithEmail -> onLogInWithEmail(
                event.openAndPopUp,
                event.openScreen
            )

            is AuthUiEvents.OnSignUpWithEmail -> onSignUpWithEmail(event.popUp)
            is AuthUiEvents.OnSignInWithGoogle -> onSignInWithGoogle(
                event.credential,
                event.openAndPopUp
            )

            AuthUiEvents.OnSignOut -> signOut()
            AuthUiEvents.OnResendVerificationLink -> resendVerificationLink()
        }
    }

    // Update the email verification status of the user
    private fun updateEmailVerificationStatus() {
        firebaseAuth.currentUser?.let {
            _isEmailVerified.value = it.isEmailVerified
        }
    }

    // Update functions
    private fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    private fun updateLoadingStatus(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    private fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    private fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    private fun toggleConfirmPasswordVisibility() {
        _uiState.value =
            _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    // Observe email verification status
    suspend fun observeEmailVerification(onEmailVerified: (Any, Any) -> Unit) {
        var currentUser: FirebaseUser? = null

        accountService.currentUser.collect { user ->
            currentUser = user
        }

        while (!_isEmailVerified.value) {
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

    // Handle login with email and password
    private fun onLogInWithEmail(openAndPopUp: (Any, Any) -> Unit, openScreen: (Any) -> Unit) {
        launchCatching {
            validateEmailAndPassword() // Validation helper

            updateLoadingStatus(true)

            accountService.signInWithEmail(_uiState.value.email, _uiState.value.password)

            updateLoadingStatus(false)

            updateEmailVerificationStatus()
            if (!_isEmailVerified.value) {
                openScreen(Graphs.AuthGraph.VerificationDialog)
                accountService.verifyUserAccount(_uiState.value.email, _uiState.value.password)
            } else {
                openAndPopUp(Graphs.HomeGraph, Graphs.AuthGraph)
            }
        }
    }

    // Handle sign-up with email and password
    private fun onSignUpWithEmail(popUp: () -> Unit) {
        launchCatching {
            validateEmailForSignUp() // Validation helper
            updateLoadingStatus(true)
            accountService.signUpWithEmail(_uiState.value.email, _uiState.value.password)

            updateLoadingStatus(false)

            signOut() // Sign out the user after signup
            popUp()
            resetState()
        }
    }

    // Handle sign-in with Google credentials
    private fun onSignInWithGoogle(credential: Credential, openAndPopUp: (Any, Any) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                openAndPopUp(Graphs.HomeGraph, Graphs.AuthGraph)
            } else {
                Log.d("auth", "Unexpected Credential")
            }
        }
    }

    // Helper function to resend verification link
    private fun resendVerificationLink() {
        launchCatching {
            accountService.verifyUserAccount(_uiState.value.email, _uiState.value.password)
        }
    }

    // Sign out the current user
    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    // Reset the UI state
    fun resetState() {
        _uiState.value = AuthScreenState()
    }

    // Validation for login and sign-up
    private fun validateEmailAndPassword() {
        if (_uiState.value.email.isBlank()) throw IllegalArgumentException("Email cannot be blank")
        if (_uiState.value.password.isBlank()) throw IllegalArgumentException("Password cannot be blank")
    }

    private fun validateEmailForSignUp() {
        if (!_uiState.value.email.isValidEmail()) throw IllegalArgumentException("Invalid Email " + _uiState.value.email)
        if (_uiState.value.password != _uiState.value.confirmPassword) throw IllegalArgumentException(
            "Passwords do not match"
        )
    }

    // Send snackbar messages
    private fun sendSnackbarMessage(
        message: String,
        snackbarDuration: SnackbarDuration = SnackbarDuration.Short
    ) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                SnackbarEvent(message = message, duration = snackbarDuration)
            )
        }
    }

    // Helper function for handling exceptions
    private fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleException(throwable)
        }, block = block)

    // Centralized exception handling
    private fun handleException(throwable: Throwable) {
        when (throwable) {
            is FirebaseAuthException -> handleFirebaseAuthException(throwable)
            is IllegalArgumentException -> sendSnackbarMessage(throwable.message ?: "Invalid Input")
            else -> sendSnackbarMessage(throwable.message ?: "An unknown error occurred")
        }
        updateLoadingStatus(false)
        Log.d(ERROR_TAG, throwable.message.orEmpty())
    }

    // Handle Firebase-specific exceptions
    private fun handleFirebaseAuthException(exception: FirebaseAuthException) {
        when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> sendSnackbarMessage("The email address is badly formatted.")
            "ERROR_INVALID_CREDENTIAL" -> sendSnackbarMessage("The credentials are incorrect.")
            "ERROR_EMAIL_ALREADY_IN_USE" -> sendSnackbarMessage("This email is already in use.")
            "ERROR_WRONG_PASSWORD" -> sendSnackbarMessage("Incorrect password.")
            "ERROR_WEAK_PASSWORD" -> sendSnackbarMessage("Password is too weak.")
            "ERROR_USER_NOT_FOUND" -> sendSnackbarMessage("No account found with this email.")
            "ERROR_TOO_MANY_REQUESTS" -> sendSnackbarMessage("Too many requests with this email.")
            else -> sendSnackbarMessage(exception.message ?: "Authentication error")
        }
    }
}
