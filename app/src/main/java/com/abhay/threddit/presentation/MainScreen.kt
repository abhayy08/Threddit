package com.abhay.threddit.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.SurfaceCoroutineScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ControlledComposition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.size.Scale
import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(modifier: Modifier = Modifier, openScreen: (String) -> Unit) {

    val viewmodel = hiltViewModel<AuthenticationViewModel>()

    Scaffold { _ ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            val asd = Firebase.auth.currentUser
//            val userId = asd?.uid ?: "NoUser"
//
//            Text(text = userId)

            Button(onClick = { openScreen(SIGN_IN_SCREEN) }) {
                Text(text = "SIGN IN")
            }

            Button(onClick = { openScreen(SIGN_UP_SCREEN) }) {
                Text(text = "SIGN UP")
            }

            Button(onClick = { viewmodel.signOut() }) {
                Text(text = "Sign Out")
            }

        }
    }

}

@Preview
@Composable
private fun Preview() {
    ThredditTheme {
        MainScreen {

        }
    }
}