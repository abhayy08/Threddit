package com.abhay.threddit.presentation.screens.main.add_post

import com.abhay.threddit.utils.getCurrentDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

data class AddPostState(
    val title: String = "",
    val content: String = "",
    val date: String = getCurrentDate()
)


