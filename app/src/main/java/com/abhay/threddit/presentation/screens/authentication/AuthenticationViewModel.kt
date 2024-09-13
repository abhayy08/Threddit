package com.abhay.threddit.presentation.screens.authentication

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.emptyCacheFontFamilyResolver
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.domain.ThredditUser
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.common.SnackbarEvent
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.utils.ERROR_TAG
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _uiState = mutableStateOf(AuthScreenState())
    val uiState: State<AuthScreenState> = _uiState

    private val _isEmailVerified = MutableStateFlow(false)

    private var usersWithSameUsername: List<ThredditUser> = emptyList()

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

            is AuthUiEvents.OnSignUpWithEmail -> onSignUpWithEmail(event.openScreen)
            is AuthUiEvents.OnSignInWithGoogle -> onSignInWithGoogle(
                event.credential,
                event.openScreen,
                event.openAndPopUp
            )

            is AuthUiEvents.OnSignOut -> signOut()
            is AuthUiEvents.OnResendVerificationLink -> resendVerificationLink()

            is AuthUiEvents.OnDisplayNameChange -> updateDisplayName(event.displayName)
            is AuthUiEvents.SaveUserDetails -> saveUserDetails(event.openAndPopUp)
            is AuthUiEvents.OnBioChange -> _uiState.value = _uiState.value.copy(bio = event.bio)
            is AuthUiEvents.OnUsernameChange -> updateUsername(event.username)
            is AuthUiEvents.OnDOBChange -> updateDob(event.dob)
        }
    }


    // Update the email verification status of the user
    private fun updateEmailVerificationStatus() {
        accountService.getFirebaseUser()?.let {
            _isEmailVerified.value = it.isEmailVerified
        }
    }

    // Update functions

    private fun updateDob(dob: String) {
        _uiState.value = _uiState.value.copy(dob = dob, dobError = false)
    }

    private var debounceJob: Job? = null

    private fun updateUsername(username: String) {
        // Cancel any previous debounce jobs to avoid multiple queries
        debounceJob?.cancel()

        // Immediately update the UI with the current input
        _uiState.value = _uiState.value.copy(username = username, usernameError = false)

        // Debounce the check to avoid firing too many Firestore queries
        debounceJob = viewModelScope.launch {
            delay(300) // Debounce delay (300ms)

            // Now check for the uniqueness of the username
            launchCatching {
                _uiState.value = _uiState.value.copy(
                    checkingUsernameUniqueness = true
                )
                usersWithSameUsername = firestoreService.getUserswithSameUsername(username)
                Log.d("UsersWithSameUsername: ", usersWithSameUsername.toString())


                // Update the UI based on whether the username is unique
                if (usersWithSameUsername.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(usernameError = true, checkingUsernameUniqueness = false)
                } else {
                    _uiState.value = _uiState.value.copy(usernameError = false, checkingUsernameUniqueness = false)
                }

            }
        }
    }

    private fun updateDisplayName(displayName: String) {
        _uiState.value = _uiState.value.copy(
            displayName = displayName,
            nameError = false
        )
    }

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
    suspend fun observeEmailVerification(openAndPopUp: (Any, Any) -> Unit) {
//        var currentUser: FirebaseUser? = null
//
//        This part is causing a infinite while loop  // CHECK LATER
//        accountService.currentUser.collect { user ->
//            Log.d("CurrentUser: ", user?.uid.orEmpty() + " " + user?.isEmailVerified)
//            currentUser = user
//        }

        val currentUser = accountService.getFirebaseUser()

        while (!_isEmailVerified.value) {
            currentUser?.reload() // Reloads the user data
//            Log.d("InsideWhileLoop: ", currentUser?.uid.orEmpty() + " " + currentUser?.isEmailVerified)
            val newVerifiedStatus = currentUser?.isEmailVerified ?: false
            if (newVerifiedStatus != _isEmailVerified.value) {
                _isEmailVerified.value = newVerifiedStatus
                if (_isEmailVerified.value) {
                    openAndPopUp(Graphs.AuthGraph.AddUserDetailsScreen, Graphs.AuthGraph)
                    break
                }
            }
            delay(3000)
        }
    }

    private fun saveUserDetails(openAndPopUp: (Any, Any) -> Unit) {
        launchCatching {

            if (_uiState.value.displayName.isEmpty() || _uiState.value.username.isEmpty() || _uiState.value.dob.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    usernameError = _uiState.value.username.isEmpty(),
                    nameError = _uiState.value.displayName.isEmpty(),
                    dobError = _uiState.value.dob.isEmpty(),
                )
                throw IllegalArgumentException("Fields cannot be empty cannot be empty")
            }
            if(_uiState.value.usernameError) {
                throw IllegalArgumentException("Username should be unique")
            }

            accountService.updateDisplayName(_uiState.value.displayName)
            firestoreService.createUserInFireStore(
                name = _uiState.value.displayName,
                username = _uiState.value.username,
                email = _uiState.value.email,
                bio = _uiState.value.bio,
                userId = accountService.currentUserId,
                dob = _uiState.value.dob,
                profilePicUrl = null,
                followers = 0,
                following = 0

            )
            openAndPopUp(Graphs.MainNavGraph, Graphs.AuthGraph)
        }
    }

    // Handle login with email and password
    private fun onLogInWithEmail(openAndPopUp: (Any, Any) -> Unit, openScreen: (Any) -> Unit) {
        launchCatching {
            validateEmailAndPassword() // Validation helper

            updateLoadingStatus(true)

            accountService.signInWithEmail(_uiState.value.email, _uiState.value.password)
            updateEmailVerificationStatus()

            updateLoadingStatus(false)

            if (!_isEmailVerified.value) {
                accountService.verifyUserAccount(_uiState.value.email, _uiState.value.password)
                openScreen(Graphs.AuthGraph.VerificationDialog)
            } else if (accountService.getFirebaseUser()!!.displayName.isNullOrEmpty()) {
                openScreen(Graphs.AuthGraph.AddUserDetailsScreen)
            } else {
                openAndPopUp(Graphs.MainNavGraph, Graphs.AuthGraph)
            }
        }
    }

    // Handle sign-up with email and password
    private fun onSignUpWithEmail(openScreen: (Any) -> Unit) {
        launchCatching {
            validateEmailForSignUp() // Validation helper
            updateLoadingStatus(true)
            accountService.signUpWithEmail(_uiState.value.email, _uiState.value.password)
            accountService.updateDisplayName(_uiState.value.displayName)
            updateLoadingStatus(false)
            sendSnackbarMessage("Account Created!")
            openScreen(Graphs.AuthGraph.VerificationDialog)
//            signOut() // Sign out the user after signup
        }
    }

    // Handle sign-in with Google credentials
    private fun onSignInWithGoogle(
        credential: Credential,
        openScreen: (Any) -> Unit,
        openAndPopUp: (Any, Any) -> Unit
    ) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)

                val currentUser = accountService.getFirebaseUser()

                openScreen(Graphs.AuthGraph.AddUserDetailsScreen)

                _uiState.value = _uiState.value.copy(
                    displayName = currentUser?.displayName ?: "",
                    email = currentUser?.email ?: "",

                )

//                openAndPopUp(Graphs.MainNavGraph, Graphs.AuthGraph)

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
        Log.d("AUTH ERROR", "${exception.errorCode}")

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
