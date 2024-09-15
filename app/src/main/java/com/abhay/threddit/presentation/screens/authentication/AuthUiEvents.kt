package com.abhay.threddit.presentation.screens.authentication

import android.net.Uri
import androidx.credentials.Credential
import com.google.android.gms.auth.api.Auth

sealed class AuthUiEvents {

    // Login - SignUp Ui Events
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
    data object OnResendVerificationLink : AuthUiEvents()
    data class OnEmailChange(val email: String) : AuthUiEvents()
    data class OnPasswordChange(val password: String) : AuthUiEvents()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthUiEvents()
    data object OnPasswordVisibilityChange : AuthUiEvents()
    data object OnConfirmPasswordVisibilityChange : AuthUiEvents()

    data object OnSignOut : AuthUiEvents()

    // Add User Details Ui Events
    data class OnDisplayNameChange(val displayName: String): AuthUiEvents()
    data class OnUsernameChange(val username: String): AuthUiEvents()
    data class OnDOBChange(val dob: String): AuthUiEvents()
    data class OnBioChange(val bio: String): AuthUiEvents()
    data class SaveUserDetails(val openAndPopUp: (Any, Any) -> Unit) : AuthUiEvents()
    data class OnAddImageClick(val uri: Uri): AuthUiEvents()


}

