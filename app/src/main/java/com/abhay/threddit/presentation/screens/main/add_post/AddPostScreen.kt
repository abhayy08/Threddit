package com.abhay.threddit.presentation.screens.main.add_post

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.ui.theme.ThredditTheme

@Composable
fun AddPostScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AddPostTopAppbar()
            HorizontalDivider()
            NewPost(

            )
        }
    }
}

@Composable
fun NewPost(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: () -> Unit = {}
) {
    val placeholderImage =
        if (isSystemInDarkTheme()) R.drawable.default_pfp_light else R.drawable.default_pfp_dark
    Row(
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = placeholderImage), contentDescription = "Profile Pic",
            modifier = Modifier
                .size(65.dp)
                .padding(horizontal = 8.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "abhayy_08_",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = {},
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(text = "Placeholder")
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostTopAppbar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier.padding(horizontal = 4.dp),
        title = {
            Text(
                text = stringResource(R.string.new_post),
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "back")
            }
        }
    )
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AddPostPreview() {
    ThredditTheme {
        AddPostScreen()
    }
}