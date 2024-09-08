package com.abhay.threddit.presentation.authentication

import androidx.credentials.Credential

sealed class AuthUiEvents {

    data class OnLogInWithEmail(
        val openAndPopUp: (Any, Any) -> Unit,
        val openScreen: (Any) -> Unit
    ) : AuthUiEvents()

    data class OnSignUpWithEmail(val popUp: () -> Unit) : AuthUiEvents()
    data class OnSignInWithGoogle(
        val credential: Credential,
        val openAndPopUp: (Any, Any) -> Unit
    ) : AuthUiEvents()

    data object OnSignOut : AuthUiEvents()
    data object OnResendVerificationLink : AuthUiEvents()

    data class OnEmailChange(val email: String) : AuthUiEvents()
    data class OnPasswordChange(val password: String) : AuthUiEvents()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthUiEvents()
    data object OnPasswordVisibilityChange : AuthUiEvents()
    data object OnConfirmPasswordVisibilityChange : AuthUiEvents()

}
