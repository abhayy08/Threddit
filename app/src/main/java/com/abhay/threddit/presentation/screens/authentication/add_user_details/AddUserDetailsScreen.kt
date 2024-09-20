package com.abhay.threddit.presentation.screens.authentication.add_user_details

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhay.threddit.R
import com.abhay.threddit.presentation.components.CustomTextField
import com.abhay.threddit.presentation.screens.authentication.AuthUiEvents
import com.abhay.threddit.ui.theme.ThredditShadeFont
import com.abhay.threddit.ui.theme.ThredditTheme

@Composable
fun AddUserDetailsScreen(
    modifier: Modifier = Modifier,
    state: UserDetailsState = UserDetailsState(),
    onEvent: (AuthUiEvents) -> Unit = {},
    onOpenAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    popUp: () -> Unit = {},
    onSignOut: () -> Unit
) {
//    val state = viewModel.uiState.value

    BackHandler {
        onSignOut()
        popUp()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onEvent(AuthUiEvents.OnAddImageClick(uri))
            }
        }
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
    )   {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.complete_your_profile),
                style = MaterialTheme.typography.displayMedium,
                fontFamily = ThredditShadeFont
            )


            val placeholderImage =
                if (isSystemInDarkTheme()) R.drawable.default_pfp_light else R.drawable.default_pfp_dark

            AsyncImage(
                model = state.profilePicUri ?: placeholderImage,
                contentDescription = "Profile Pic",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .size(130.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )

            Button(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(text = "Add Profile Image")
            }


            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = state.displayName, onValueChange = {
                        onEvent(AuthUiEvents.OnDisplayNameChange(it))
                    }, modifier = Modifier
                        .fillMaxWidth(), label = "Name",
                    isError = state.nameError
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.username,
                    onValueChange = {
                        onEvent(AuthUiEvents.OnUsernameChange(it))
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
                    } else {
                        null
                    },
                    trailingIcon = if (state.checkingUsernameUniqueness) {
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
                        onEvent(AuthUiEvents.OnDOBChange(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(), label = "Date of Birth",
                    isError = state.dobError
                )

                Spacer(modifier = Modifier.height(8.dp))


                CustomTextField(
                    value = state.bio, onValueChange = {
                        onEvent(AuthUiEvents.OnBioChange(it))
                    }, modifier = Modifier
                        .fillMaxWidth(), label = "Bio",
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 15.dp)
                    .height(45.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = { onEvent(AuthUiEvents.SaveUserDetails(onOpenAndPopUp)) }) {
                    Text(text = "Proceed")
                }
            }
        }
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun Aasd() {
    ThredditTheme {
        AddUserDetailsScreen(
            onSignOut = {}
        )
    }
}