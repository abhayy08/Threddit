package com.abhay.threddit.presentation.screens.main.add_post

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhay.threddit.R
import com.abhay.threddit.domain.ThredditUser
import com.abhay.threddit.presentation.components.CustomButton
import com.abhay.threddit.ui.theme.ThredditTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddPostScreen(
    modifier: Modifier = Modifier,
    state: AddPostState,
    onEvent: (AddPostEvents) -> Unit = {},
    openAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    thredditUserFlow: StateFlow<ThredditUser>,
) {
    val thredditUser by thredditUserFlow.collectAsState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Post",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(12.dp))
            val placeholderImage =
                if (isSystemInDarkTheme()) R.drawable.default_pfp_light else R.drawable.default_pfp_dark

            Row(
                modifier = modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = thredditUser.profilePicUrl ?: placeholderImage,
                        contentDescription = "Profile Pic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .size(55.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = thredditUser.username,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .defaultMinSize(minHeight = 50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.title,
                        onValueChange = { onEvent(AddPostEvents.OnTitleChange(it)) },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    if (state.title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .defaultMinSize(minHeight = 200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.padding(14.dp)
                ) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.content,
                        onValueChange = { onEvent(AddPostEvents.OnContentChange(it)) },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    if (state.content.isEmpty()) {
                        Text(
                            text = "What's on your mind ?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                }
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { onEvent(AddPostEvents.OnAddPost(thredditUser.username, openAndPopUp)) }
            ) {
                Text(text = "Post", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}


//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//private fun AddPostPreview() {
//    ThredditTheme {
//        AddPostScreen(
//            state = AddPostState(),
//            thredditUserFlow = StateFlow<ThredditUser>
//        )
//    }
//}