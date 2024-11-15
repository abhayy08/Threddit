package com.abhay.threddit.presentation.screens.main.add_post

sealed class AddPostEvents {
    data class OnTitleChange(val title: String): AddPostEvents()
    data class OnContentChange(val content: String): AddPostEvents()

    data class OnAddPost(val username: String, val openAndPopUp: (Any, Any) -> Unit): AddPostEvents()

}