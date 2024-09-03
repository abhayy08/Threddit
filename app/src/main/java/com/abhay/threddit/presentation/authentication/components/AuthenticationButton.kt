package com.abhay.threddit.presentation.authentication.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import coil.ImageLoader
import com.abhay.threddit.R
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

@Composable
fun AuthenticationButton(
    modifier: Modifier = Modifier,
    buttonText: Int,
    onGetCredentialResponse: (Credential) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)



    Button(
        modifier = modifier,
        onClick = {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )

                    onGetCredentialResponse(result.credential)

                }catch (e: Exception) {
                    Log.d("Auth", e.message.orEmpty())
                }
            }
        }
    ) {
        Text(text = stringResource(id = buttonText))
    }
}

@Preview
@Composable
private fun AuthButtonPrev() {
    ThredditTheme {
        Surface {
            AuthenticationButton(
                buttonText = R.string.sign_in_with_google,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(0.95f)
            ) {

            }
        }
    }
}