package com.abhay.threddit.presentation.screens.authentication.add_user_details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.presentation.components.CustomTextField
import com.abhay.threddit.presentation.screens.authentication.AuthUiEvents
import com.abhay.threddit.presentation.screens.authentication.AuthenticationViewModel
import com.abhay.threddit.ui.theme.ThredditShadeFont
import com.abhay.threddit.ui.theme.ThredditTheme

@Composable
fun AddUserDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel,
    onOpenAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    popUp: () -> Unit = {}
) {
    val state = viewModel.uiState.value

    BackHandler {
        viewModel.signOut()
        popUp()
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.complete_your_profile),
                style = MaterialTheme.typography.displayLarge,
                fontFamily = ThredditShadeFont
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = state.displayName, onValueChange = {
                        viewModel.onEvent(AuthUiEvents.OnDisplayNameChange(it))
                    }, modifier = Modifier
                        .fillMaxWidth(), label = "Name",
                    isError = state.nameError
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.username,
                    onValueChange = {
                        viewModel.onEvent(AuthUiEvents.OnUsernameChange(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = "Username",
                    isError = state.usernameError,
                    supportingText = if (state.usernameError) {
                        {
                            Text(
                                modifier = Modifier.padding(vertical = 2.dp),
                                text = "username should be unique",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }else {
                        null
                    },
                    trailingIcon = if(state.checkingUsernameUniqueness) {
                        {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .size(25.dp),
                                color = if (isSystemInDarkTheme()) Color.White else Color.DarkGray
                            )
                        }
                    } else {
                        null
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.dob,
                    onValueChange = {
                        viewModel.onEvent(AuthUiEvents.OnDOBChange(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(), label = "Date of Birth",
                    isError = state.dobError
                )

                Spacer(modifier = Modifier.height(8.dp))


                CustomTextField(
                    value = state.bio, onValueChange = {
                        viewModel.onEvent(AuthUiEvents.OnBioChange(it))
                    }, modifier = Modifier
                        .fillMaxWidth(), label = "Bio"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 10.dp)
                    .shadow(10.dp, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = { viewModel.onEvent(AuthUiEvents.SaveUserDetails(onOpenAndPopUp)) }) {
                    Text(text = "Proceed")
                }
            }
        }
    }
}


@Preview
@Composable
private fun Aasd() {
    ThredditTheme {
//        AddUserDetailsScreen(
//            viewModel = AuthenticationViewModel(
//                AccountServiceImpl(),
//                FirebaseAuth.getInstance()
//            )
//        )
    }
}