package com.abhay.threddit.presentation.screens.authentication

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.data.firebase.auth.AccountServiceImpl
import com.abhay.threddit.data.firebase.firestore.FirestoreServiceImpl
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@Composable
fun VerificationScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel,
    openAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    popUp: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        viewModel.observeEmailVerification(openAndPopUp)
    }

    BackHandler {
        viewModel.signOut()
        popUp()
    }

    val state = viewModel.authUiState.collectAsState().value

    Surface(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(0.9f), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = stringResource(R.string.mail_sent)
            )

            Text(
                text = stringResource(R.string.verify_your_email_address),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = stringResource(R.string.we_ve_sent_a_verification_email_to),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                modifier = Modifier.padding(bottom = 12.dp),
                text = state.email,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.click_on_the_link_to_complete_the_verification_process),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.you_might_need_to_check_your_spam_folder),
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { viewModel.onEvent(AuthUiEvents.OnResendVerificationLink) },
                    modifier = Modifier.padding(12.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = stringResource(R.string.resend_email))
                }
            }

        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun VerificationScreenPreview() {
    ThredditTheme {
        VerificationScreen(
            viewModel = AuthenticationViewModel(
                accountService = AccountServiceImpl(),
                firestoreService = FirestoreServiceImpl(
                    db = Firebase.firestore,
                    auth = Firebase.auth,
                    storage = Firebase.storage
                )
            )
        )

    }
}