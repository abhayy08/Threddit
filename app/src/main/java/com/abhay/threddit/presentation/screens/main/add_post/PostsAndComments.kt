package com.abhay.threddit.presentation.screens.main.add_post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhay.threddit.domain.Post
import com.abhay.threddit.presentation.components.Post

@Composable
fun Posts(
    modifier: Modifier = Modifier,
    postList: List<Post> = emptyList()
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (postList.isEmpty()) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(25.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                items(postList) { post ->
                    Post(
                        username = "abhayy08",
                        title = post.title,
                        content = post.content,
                        date = post.date
                    )
                }
            }

        }
    }

}

@Composable
fun Comments(
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "To be implemented")
    }
}