package com.abhay.threddit.presentation.screens.main.add_post

sealed class AddPostEvents {
    data class onContentChange(val content: String): AddPostEvents()

    data class onAddPost(val openAndPopUp: (Any, Any) -> Unit): AddPostEvents()

}