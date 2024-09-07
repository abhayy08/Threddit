package com.abhay.threddit.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhay.threddit.presentation.authentication.AuthenticationViewModel
import com.abhay.threddit.presentation.navigation.changes.Graphs
import com.abhay.threddit.ui.theme.ThredditTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(modifier: Modifier = Modifier, openAndPopUp: (String, String) -> Unit) {

    val viewmodel = hiltViewModel<AuthenticationViewModel>()

    Scaffold { _ ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val asd = Firebase.auth.currentUser
            val userId = asd?.uid ?: "NoUser"

            Text(text = userId)

            Button(onClick = {
                viewmodel.signOut()
                openAndPopUp(Graphs.AuthGraph.route, Graphs.HomeGraph.route)
            }) {
                Text(text = "Sign Out")
            }

        }
    }

}

@Preview
@Composable
private fun Preview() {
    ThredditTheme {
//        MainScreen {
//
//        }
    }
}