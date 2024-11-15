package com.abhay.threddit.presentation.screens.main.profile.profile_screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhay.threddit.R
import com.abhay.threddit.domain.ThredditUser
import com.abhay.threddit.presentation.screens.main.add_post.Comments
import com.abhay.threddit.presentation.screens.main.add_post.Posts
import com.abhay.threddit.ui.theme.ThredditTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userProfile: ThredditUser? = ThredditUser(),
    profileState: ProfileState = ProfileState(),
    uploadProfileImage: (Uri) -> Unit = {},
    loadProfile: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        loadProfile()
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            if (userProfile == null) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(25.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            userProfile?.let { user ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        UserInfo(
                            modifier = Modifier.fillMaxWidth(),
                            name = user.displayName,
                            username = "@${user.username}",
                            profilePicUrl = user.profilePicUrl,
                            bio = user.bio,
                            followers = user.followers
                        )
                        EditAndShareButton(modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 12.dp),
        //                            viewModel = viewModel
                            uploadProfileImage = { uploadProfileImage(it) })

                    }
                    PostsAndComments(
                        modifier = Modifier.fillMaxSize(),
                        profileState = profileState
                    )
                }
            }
        }
    }
}

@Composable
fun EditAndShareButton(
    modifier: Modifier = Modifier,
//    viewModel: ProfileViewModel,
    uploadProfileImage: (Uri) -> Unit
) {

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    uploadProfileImage(uri)
                }
            })

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(modifier = Modifier
            .weight(1f)
            .wrapContentHeight()
            .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
            Text(
                modifier = Modifier.padding(0.dp),
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(modifier = Modifier
            .weight(1f)
            .wrapContentHeight()
            .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(0.3f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = { /*TODO*/ }) {
            Text(
                modifier = Modifier.padding(0.dp),
                text = stringResource(R.string.share_profile),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsAndComments(
    modifier: Modifier = Modifier,
    profileState: ProfileState,
//    selectedTabIndex: Int = 0,
) {
    val tabs = listOf(
        "Posts", "Comments"
    )
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    val pagerState = rememberPagerState {
        tabs.size
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage

    }

    Column {
        SecondaryTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = 0,
            containerColor = Color.Transparent,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(
                            selectedTabIndex, matchContentSize = false
                        )
                        .clip(RoundedCornerShape(50)), color = MaterialTheme.colorScheme.onSurface
                )
            },
        ) {
            tabs.forEachIndexed { index, _ ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = tabs[index],
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        HorizontalPager(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f), state = pagerState
        ) { index ->
            when(tabs[index]) {
                "Posts" -> {
                    Posts(postList = profileState.posts)
                }
                "Comments" -> {
                    Comments()
                }
            }
        }
    }
}

@Composable
fun UserInfo(
    modifier: Modifier = Modifier,
    name: String,
    username: String,
    profilePicUrl: String? = null,
    bio: String? = null,
    followers: Int,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.displaySmall,
                )
//                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W400
                )

                if (bio != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W200
                    )
                }

                Text(
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(vertical = 6.dp),
                    text = "$followers Followers",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W400
                )

            }

            val placeholderImage =
                if (isSystemInDarkTheme()) R.drawable.default_pfp_light else R.drawable.default_pfp_dark

            AsyncImage(
                model = profilePicUrl ?: placeholderImage,
                contentDescription = "Profile Pic",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .size(80.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )

        }

    }
}

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProfilePreview() {
    ThredditTheme {
        ProfileScreen(uploadProfileImage = {})
    }
}