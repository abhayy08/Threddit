package com.abhay.threddit.presentation.screens.authentication

import androidx.credentials.Credential

sealed class AuthUiEvents {

    data class OnLogInWithEmail(
        val openAndPopUp: (Any, Any) -> Unit,
        val openScreen: (Any) -> Unit
    ) : AuthUiEvents()

    data class OnSignUpWithEmail(val openScreen: (Any) -> Unit) : AuthUiEvents()
    data class OnSignInWithGoogle(
        val credential: Credential,
        val openScreen: (Any) -> Unit,
        val openAndPopUp: (Any, Any) -> Unit
    ) : AuthUiEvents()

    data object OnSignOut : AuthUiEvents()
    data object OnResendVerificationLink : AuthUiEvents()

    data class OnDisplayNameChange(val displayName: String): AuthUiEvents()
    data class OnUsernameChange(val username: String): AuthUiEvents()
    data class OnDOBChange(val dob: String): AuthUiEvents()
    data class OnBioChange(val bio: String): AuthUiEvents()

    data class OnEmailChange(val email: String) : AuthUiEvents()
    data class OnPasswordChange(val password: String) : AuthUiEvents()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthUiEvents()
    data object OnPasswordVisibilityChange : AuthUiEvents()
    data object OnConfirmPasswordVisibilityChange : AuthUiEvents()
    data class SaveUserDetails(val openAndPopUp: (Any, Any) -> Unit) : AuthUiEvents()


}

