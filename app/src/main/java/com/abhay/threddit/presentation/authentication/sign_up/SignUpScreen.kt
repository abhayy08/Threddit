package com.abhay.threddit.presentation.authentication.sign_up

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.presentation.authentication.AuthUiEvents
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel,
    openAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    popUp: () -> Unit
) {
    val state = viewModel.uiState.value

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.create_your_account),
                style = MaterialTheme.typography.displayLarge
            )

            EmailAndPasswordFields(
                modifier = Modifier.fillMaxWidth(),
                email = state.email,
                password = state.password,
                onEvent = viewModel::onEvent,
                onButtonClick = {
                    viewModel.onEvent(AuthUiEvents.OnSignUpWithEmail(popUp))
                },
                confirmPassword = state.confirmPassword,
                passwordVisible = state.isPasswordVisible,
                confirmPasswordVisible = state.isConfirmPasswordVisible,
                isLoading = state.isLoading
            )
        }
    }
}

@Composable
fun EmailAndPasswordFields(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    onEvent: (AuthUiEvents) -> Unit,
    onButtonClick: () -> Unit,
    isLoading: Boolean
) {

    val borderColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        focusedLabelColor = borderColor,
        unfocusedLabelColor = borderColor.copy(0.7f),
        cursorColor = borderColor
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email
        EmailTextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                ),
            onEvent = onEvent,
            email = email,
            textFieldColors = textFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        PasswordTextField(modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
            ),
            onValueChange = {
                onEvent(AuthUiEvents.OnPasswordChange(it))
            },
            password = password,
            passwordVisible = passwordVisible,
            textFieldColors = textFieldColors,
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            onPasswordVisibilityChange = {
                onEvent(AuthUiEvents.OnPasswordVisibilityChange)
            })
        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                ),
            onValueChange = {
                onEvent(AuthUiEvents.OnConfirmPasswordChange(it))
            },
            password = confirmPassword,
            passwordVisible = confirmPasswordVisible,
            textFieldColors = textFieldColors,
            label = {
                Text(text = stringResource(R.string.confirm_password))
            },
            onPasswordVisibilityChange = {
                onEvent(AuthUiEvents.OnConfirmPasswordVisibilityChange)
            })

        val indicatorColor = if (!isSystemInDarkTheme()) Color.White else Color.DarkGray

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            onClick = onButtonClick
        ) {
            Text(text = stringResource(R.string.sign_up))
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(25.dp),
                    color = indicatorColor
                )
            }
        }

    }
}

@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    onEvent: (AuthUiEvents) -> Unit,
    email: String,
    textFieldColors: TextFieldColors,
) {
    TextField(
        value = email, onValueChange = {
            onEvent(AuthUiEvents.OnEmailChange(it))
        }, modifier = modifier, label = {
            Text(text = stringResource(id = R.string.email))
        }, colors = textFieldColors
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    password: String,
    label: @Composable () -> Unit,
    passwordVisible: Boolean,
    textFieldColors: TextFieldColors,
    onPasswordVisibilityChange: () -> Unit
) {
    TextField(value = password,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        colors = textFieldColors,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {

            val icon = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val dark = isSystemInDarkTheme()
            val color = if (dark) {
                Color.White
            } else {
                Color.DarkGray
            }

            IconButton(onClick = onPasswordVisibilityChange) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Toggle password Visibility",
                    tint = color
                )
            }
        }
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SignUpPrev() {
    ThredditTheme {

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
            SignUpScreen(viewModel = AuthenticationViewModel(
                accountService = AccountServiceImpl(),
                firebaseAuth = FirebaseAuth.getInstance()
            ), openAndPopUp = { any: Any, any1: Any -> }, popUp = {})
        }
    }
}