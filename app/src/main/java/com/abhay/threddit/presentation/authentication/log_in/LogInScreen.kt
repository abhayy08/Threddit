package com.abhay.threddit.presentation.authentication.log_in

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.presentation.authentication.AuthUiEvents
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.authentication.components.AuthenticationButton
import com.abhay.threddit.presentation.authentication.components.OrDivider
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged


@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel,
    openAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    openScreen: (Any) -> Unit
) {

    val state = viewModel.uiState.value

    val passwordVisible = state.isPasswordVisible

    val labelAndCursorColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray

    val indicatorColor =  if (!isSystemInDarkTheme()) Color.White else Color.DarkGray

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        focusedLabelColor = labelAndCursorColor,
        unfocusedLabelColor = labelAndCursorColor.copy(0.7f),
        cursorColor = labelAndCursorColor
    )
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(id = R.string.log_in_title),
                style = MaterialTheme.typography.displayLarge
            )

            //Email
            TextField(
                value = state.email,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvents.OnEmailChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                    ),
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password
            TextField(
                value = state.password,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvents.OnPasswordChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                    ),
                colors = textFieldColors,
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
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
                        viewModel.onEvent(AuthUiEvents.OnPasswordVisibilityChange)
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Toggle password Visibility",
                            tint = color
                        )
                    }
                }
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.don_t_have_an_account),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.create_account),
                    modifier = Modifier.clickable {
                        openScreen(Graphs.AuthGraph.SignUpScreen)
                        viewModel.resetState()
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

            }

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
                onClick = {
                    viewModel.onEvent(AuthUiEvents.OnLogInWithEmail(openAndPopUp, openScreen))
                },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,

                ) {
                    Text(text = stringResource(R.string.login))
                    if(state.isLoading){
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


            Spacer(modifier = Modifier.height(12.dp))
            OrDivider()

            Spacer(modifier = Modifier.height(12.dp))

            AuthenticationButton(
                modifier = Modifier.fillMaxWidth(),
                buttonText = R.string.sign_in_with_google
            ) { credential ->
                viewModel.onEvent(AuthUiEvents.OnSignInWithGoogle(credential, openScreen, openAndPopUp))
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
        LogInScreen(viewModel = AuthenticationViewModel(
            accountService = AccountServiceImpl(),
            firebaseAuth = FirebaseAuth.getInstance()
        ), openAndPopUp = { any: Any, any1: Any -> }, openScreen = {})
    }
}