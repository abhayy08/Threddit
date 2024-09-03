package com.abhay.threddit.presentation.authentication.sign_in

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.abhay.threddit.R
import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.authentication.components.AuthenticationButton
import com.abhay.threddit.presentation.authentication.components.OrDivider
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String, String) -> Unit = { _,_ ->}
) {

    val viewModel = AuthenticationViewModel(AccountServiceImpl())

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val borderColor = if (isSystemInDarkTheme()) Color.White else Color.DarkGray

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


    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = ""
                )
            }
        })
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(id = R.string.log_in_title),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            //Email
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, shape = RoundedCornerShape(4.dp), color = borderColor),
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password
            TextField(value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, shape = RoundedCornerShape(4.dp), color = borderColor),
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
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

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Toggle password Visibility",
                            tint = color
                        )
                    }
                })
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                onClick = { /*TODO*/ },
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(12.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(12.dp))

            AuthenticationButton(
                modifier = Modifier.fillMaxWidth(),
                buttonText = R.string.sign_in_with_google
            ) { credential ->
                viewModel.onSignInWithGoogle(credential, openAndPopUp)
            }
        }
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AuthScreenPreview() {
    ThredditTheme {
        SignInScreen()
    }
}