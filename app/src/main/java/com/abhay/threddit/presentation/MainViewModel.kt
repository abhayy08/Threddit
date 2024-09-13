package com.abhay.threddit.presentation

import androidx.lifecycle.ViewModel
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getStartDestination(): Any {
        return if (isUserLoggedIn()) {
            Graphs.MainNavGraph
        } else {
            Graphs.AuthGraph
        }
    }

}