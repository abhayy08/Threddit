package com.abhay.threddit.presentation.screens.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.abhay.threddit.data.firebase.auth.AccountServiceImpl
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AddUserDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel,
    onOpenAndPopUp: (Any, Any) -> Unit = {_,_ -> }
) {


    BackHandler {
        viewModel.signOut()
    }

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

    Surface(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                name = viewModel.uiState.value.displayName,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvents.OnDisplayNameChange(it))
                },
                textFieldColors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                    ),
                label = "Name"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                name = viewModel.uiState.value.username,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvents.OnUsernameChange(it))
                },
                textFieldColors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                    ),
                label = "Username"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                name = viewModel.uiState.value.bio,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvents.OnBioChange(it))
                },
                textFieldColors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary.copy(0.3f)
                    ),
                label = "Bio"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 10.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                onClick = { viewModel.onEvent(AuthUiEvents.SaveUserDetails(onOpenAndPopUp)) }
            ) {
                Text(text = "Save Name")
            }
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    name: String,
    textFieldColors: TextFieldColors,
    label: String = "",
) {
    TextField(
        value = name, onValueChange = {
            onValueChange(it)
        }, modifier = modifier, label = {
            Text(text = label)
        }, colors = textFieldColors
    )
}

@Preview
@Composable
private fun asd() {
    ThredditTheme {
        AddUserDetailsScreen(
            viewModel = AuthenticationViewModel(
                AccountServiceImpl(),
                Firebase.auth
            )
        )
    }
}