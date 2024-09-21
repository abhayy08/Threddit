package com.abhay.threddit.presentation.screens.authentication

import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarDuration
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
import com.abhay.threddit.presentation.screens.authentication.add_user_details.UserDetailsState
import com.abhay.threddit.utils.ERROR_TAG
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _authUiState = MutableStateFlow(AuthScreenState())
    val authUiState: StateFlow<AuthScreenState> = _authUiState.asStateFlow()

    private val _userDetailState = MutableStateFlow(UserDetailsState())
    val userDetailState: StateFlow<UserDetailsState> = _userDetailState.asStateFlow()

    private val _isEmailVerified = MutableStateFlow(false)

    private var usersWithSameUsername: List<ThredditUser> = emptyList()

    private var thredditUser: ThredditUser? = null

    init {
        collectThredditUser()
    }


    /*
    CHANGE TO USING STATEFLOW FOR UI STATE MANAGEMENT
    CHANGE UPDATING STATE LIKE FOLLOWS

    _userDetailState.value = _userDetailState.value.copy(profilePicUri = imageUri)

    TO

    _userDetailState.update{
        it.copy(
            profilePicUri = imageUri
        )
    }

     */


    private fun collectThredditUser() {
        viewModelScope.launch {
            firestoreService.getUserFlow().collect { user ->
                thredditUser = user
            }
        }
    }

    // Handle UI Events
    fun onEvent(event: AuthUiEvents) {
        when (event) {
            is AuthUiEvents.OnEmailChange -> updateEmail(event.email)
            is AuthUiEvents.OnPasswordChange -> updatePassword(event.password)
            is AuthUiEvents.OnConfirmPasswordChange -> updateConfirmPassword(event.confirmPassword)
            is AuthUiEvents.OnPasswordVisibilityChange -> toggleVisibility(VisibilityType.PASSWORD)
            is AuthUiEvents.OnConfirmPasswordVisibilityChange -> toggleVisibility(VisibilityType.CONFIRM_PASSWORD)
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
            is AuthUiEvents.OnBioChange -> _userDetailState.update { it.copy(bio = event.bio) }

            is AuthUiEvents.OnUsernameChange -> updateUsername(event.username)
            is AuthUiEvents.OnDOBChange -> updateDob(event.dob)
            is AuthUiEvents.OnAddImageClick -> addProfilePic(event.uri)
        }
    }

    // Adding UserProfile Pic
    private fun addProfilePic(imageUri: Uri) {
//        firestoreService.uploadProfileImage(imageUri = imageUri)
        _userDetailState.update { it.copy(profilePicUri = imageUri) }
    }


    // Update the email verification status of the user
    private fun updateEmailVerificationStatus() {
        accountService.getFirebaseUser()?.let {
            _isEmailVerified.value = it.isEmailVerified
        }
    }

    // Update functions
    private fun updateDob(dob: String) {
        _userDetailState.update { it.copy(dob = dob, dobError = false) }
    }

    private var debounceJob: Job? = null

    private fun updateUsername(username: String) {
        // Cancel any previous debounce jobs to avoid multiple queries
        debounceJob?.cancel()

        // Immediately update the UI with the current input
        _userDetailState.update {
            it.copy(
                username = username,
                usernameError = false
            )
        }

        // Debounce the Firestore query
        debounceJob = viewModelScope.launch {
            delay(300) // Debounce delay (300ms)

            // Now check for the uniqueness of the username
            _userDetailState.update { it.copy(checkingUsernameUniqueness = true) }

            val isUsernameTaken = runCatching {
                firestoreService.getUserswithSameUsername(username).isNotEmpty()
            }.getOrDefault(false)

            // Update the UI based on whether the username is unique
            _userDetailState.update {
                it.copy(
                    usernameError = isUsernameTaken,
                    checkingUsernameUniqueness = false
                )
            }
        }
    }


    private fun updateDisplayName(displayName: String) {
        _userDetailState.update {
            it.copy(
                displayName = displayName,
                nameError = false
            )
        }
    }

    private fun updateEmail(email: String) {
        _authUiState.update { it.copy(email = email) }
    }

    private fun updateLoadingStatus(isLoading: Boolean) {
        _authUiState.update { it.copy(isLoading = isLoading) }
    }

    private fun updatePassword(password: String) {
        _authUiState.update { it.copy(password = password) }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _authUiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    private fun toggleVisibility(type: VisibilityType) {
        when (type) {
            VisibilityType.PASSWORD -> _authUiState.update { it.copy(isPasswordVisible = !_authUiState.value.isPasswordVisible) }
            VisibilityType.CONFIRM_PASSWORD -> _authUiState.update {
                it.copy(
                    isConfirmPasswordVisible = !_authUiState.value.isConfirmPasswordVisible
                )
            }
        }
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

            if (_userDetailState.value.displayName.isEmpty() || _userDetailState.value.username.isEmpty() || _userDetailState.value.dob.isEmpty()) {
                _userDetailState.update {
                    it.copy(
                        usernameError = _userDetailState.value.username.isEmpty(),
                        nameError = _userDetailState.value.displayName.isEmpty(),
                        dobError = _userDetailState.value.dob.isEmpty(),
                    )
                }
                throw IllegalArgumentException("Fields cannot be empty cannot be empty")
            }
            if (_userDetailState.value.usernameError) {
                throw IllegalArgumentException("Username should be unique")
            }
            accountService.updateDisplayName(_userDetailState.value.displayName)
            firestoreService.createUserInFireStore(
                name = _userDetailState.value.displayName,
                username = _userDetailState.value.username,
                email = _userDetailState.value.email,
                bio = _userDetailState.value.bio,
                userId = accountService.currentUserId,
                dob = _userDetailState.value.dob,
                profilePicUrl = _userDetailState.value.profilePicUri,
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

            accountService.signInWithEmail(_authUiState.value.email, _authUiState.value.password)
            updateEmailVerificationStatus()

            updateLoadingStatus(false)

            if (!_isEmailVerified.value) {
                accountService.verifyUserAccount(
                    _authUiState.value.email,
                    _authUiState.value.password
                )
                openScreen(Graphs.AuthGraph.VerificationDialog)
            } else if (!isUserCompletelyRegistered()) {
                openScreen(Graphs.AuthGraph.AddUserDetailsScreen)
            } else {
                openAndPopUp(Graphs.MainNavGraph, Graphs.AuthGraph)
            }
        }
    }

    private fun isUserCompletelyRegistered(): Boolean {
        return thredditUser?.isUserRegistered ?: false
    }

    // Handle sign-up with email and password
    private fun onSignUpWithEmail(openScreen: (Any) -> Unit) {
        launchCatching {
            validateEmailForSignUp() // Validation helper
            updateLoadingStatus(true)
            accountService.signUpWithEmail(_authUiState.value.email, _authUiState.value.password)
//            accountService.updateDisplayName(_uiState.value.displayName)
            updateLoadingStatus(false)
            sendSnackbarMessage("Account Created!")
            openScreen(Graphs.AuthGraph.VerificationDialog)
//            signOut() // Sign out the user after signup
            _userDetailState.update {
                it.copy(
                    email = _authUiState.value.email
                )
            }
        }
    }

    // Handle sign-in with Google credentials
    private fun onSignInWithGoogle(
        credential: Credential,
        openScreen: (Any) -> Unit,
        openAndPopUp: (Any, Any) -> Unit
    ) {
        launchCatching {
            if (credential !is CustomCredential || credential.type != TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Log.d("auth", "Unexpected Credential")
                return@launchCatching
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            accountService.signInWithGoogle(googleIdTokenCredential.idToken)

            if (isUserCompletelyRegistered()) {
                openAndPopUp(Graphs.MainNavGraph, Graphs.AuthGraph)
            } else {
                openScreen(Graphs.AuthGraph.AddUserDetailsScreen)
                val currentUser = accountService.getFirebaseUser()
                _userDetailState.update {
                    it.copy(
                        displayName = currentUser?.displayName.orEmpty(),
                        email = currentUser?.email.orEmpty()
                    )
                }
            }
        }
    }


    // Helper function to resend verification link
    private fun resendVerificationLink() {
        launchCatching {
            accountService.verifyUserAccount(_authUiState.value.email, _authUiState.value.password)
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
        _authUiState.value = AuthScreenState()
    }

    // Validation for login and sign-up
    private fun validateEmailAndPassword() {
        if (!_authUiState.value.email.isValidEmail()) throw IllegalArgumentException("Invalid Email " + _authUiState.value.email)
        if (_authUiState.value.email.isBlank()) throw IllegalArgumentException("Email cannot be blank")
        if (_authUiState.value.password.isBlank()) throw IllegalArgumentException("Password cannot be blank")
    }

    private fun validateEmailForSignUp() {
        if (!_authUiState.value.email.isValidEmail()) throw IllegalArgumentException("Invalid Email " + _authUiState.value.email)
        if (_authUiState.value.password != _authUiState.value.confirmPassword) throw IllegalArgumentException(
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
        val message = when (throwable) {
            is FirebaseAuthException -> handleFirebaseAuthException(throwable)
            is IllegalArgumentException -> throwable.message ?: "Invalid Input"
            else -> throwable.message ?: "An unknown error occurred"
        }
        sendSnackbarMessage(message)
        updateLoadingStatus(false)
        Log.d(ERROR_TAG, message)
    }

    private fun handleFirebaseAuthException(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_INVALID_CREDENTIAL" -> "The credentials are incorrect."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already in use."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many requests with this email."
            else -> exception.message ?: "Authentication error"
        }
    }
}

enum class VisibilityType {
    PASSWORD,
    CONFIRM_PASSWORD
}
