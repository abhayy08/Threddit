package com.abhay.threddit.presentation.authentication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhay.threddit.R
import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ThreadContextElement

@Composable
fun AddDisplayNameDialog(
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
            .height(170.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayNameTextField(
                name = viewModel.uiState.value.displayName,
                onEvent = viewModel::onEvent,
                textFieldColors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
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
                onClick = { viewModel.onEvent(AuthUiEvents.SaveDisplayName(onOpenAndPopUp)) }
            ) {
                Text(text = "Save Name")
            }
        }
    }
}

@Composable
fun DisplayNameTextField(
    modifier: Modifier = Modifier,
    onEvent: (AuthUiEvents) -> Unit,
    name: String,
    textFieldColors: TextFieldColors,
) {
    TextField(
        value = name, onValueChange = {
            onEvent(AuthUiEvents.OnDisplayNameChange(it))
        }, modifier = modifier, label = {
            Text(text = stringResource(R.string.display_name))
        }, colors = textFieldColors
    )
}

@Preview
@Composable
private fun asd() {
    ThredditTheme {
        AddDisplayNameDialog(
            viewModel = AuthenticationViewModel(
                AccountServiceImpl(),
                FirebaseAuth.getInstance()
            )
        )
    }
}