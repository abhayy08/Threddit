package com.abhay.threddit.presentation.screens.main.add_post

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhay.threddit.domain.FirestoreService
import com.abhay.threddit.presentation.common.SnackbarController
import com.abhay.threddit.presentation.common.SnackbarEvent
import com.abhay.threddit.presentation.navigation.routes.Graphs
import com.abhay.threddit.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _addPostState = mutableStateOf(AddPostState())
    val addPostState: State<AddPostState> = _addPostState

    fun onEvent(event: AddPostEvents) {
        when (event) {
            is AddPostEvents.OnTitleChange -> {
                _addPostState.value = _addPostState.value.copy(title = event.title)
            }

            is AddPostEvents.OnContentChange -> {
                _addPostState.value = _addPostState.value.copy(content = event.content)
            }

            is AddPostEvents.OnAddPost -> addPost(event.username, event.openAndPopUp)
        }
    }

    private fun addPost(username: String, openAndPopUp: (Any, Any) -> Unit) {
        firestoreService.addPost(
            username = username,
            title = _addPostState.value.title,
            content = _addPostState.value.content,
            date = getCurrentDate()
        ) { isSuccessful ->
            if (isSuccessful) {
                openAndPopUp(
                    Graphs.MainNavGraph.Profile.ProfileScreen,
                    Graphs.MainNavGraph.AddPost.AddPostScreen
                )
            } else {
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = "Couldn't add post", duration = SnackbarDuration.Short
                        )
                    )
                }
            }

        }
    }
}
