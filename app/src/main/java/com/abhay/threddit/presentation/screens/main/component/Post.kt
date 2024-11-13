package com.abhay.threddit.presentation.screens.main.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.R
import com.abhay.threddit.ui.theme.ThredditTheme

@Composable
fun Post(
    modifier: Modifier = Modifier,
    username: String,
    userProfilePic: Painter,
    title: String,
    content: String? = null,
    dateAdded: String,
    upVotes: Int,
    downVotes: Int,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = userProfilePic,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = username,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    DotDivider(modifier = Modifier.size(3.dp))
                    Text(
                        text = dateAdded,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "Menu")
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            if (content != null) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            PostInteractionButtons(
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun PostInteractionButtons(
    modifier: Modifier = Modifier,
    upVotes: Int = 145,
    downVotes: Int = 1
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .scale(0.8f)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    RoundedCornerShape(8.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable {
                    /*TODO*/
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (upVotes != 0) {
                    Text(
                        text = "${upVotes - downVotes}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    )
                }
                Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = "UpVote")
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(horizontal = 6.dp)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowDownward, contentDescription = "UpVote")
            }
        }
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .scale(0.8f)
                .padding(horizontal = 6.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.Comment, contentDescription = "UpVote")
        }
    }
}


@Composable
fun DotDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(4.dp)
            .background(MaterialTheme.colorScheme.onSecondary, shape = CircleShape)
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PostPreview() {
    ThredditTheme {
        Post(
            username = "abhayy_08",
            title = "Is Android better or am I out of touch?",
            content = "Hey everyone, I'm sorry to be coming with a negative thread, but i just need to vent" +
                    " and hear someone else's opinion. First of all, I'd like to point out that I've been an Android " +
                    "Developer since the Eclipse days, so I've seen the platform evolve, like many of you here.",
            dateAdded = "15h",
            upVotes = 150,
            downVotes = 1,
            userProfilePic = painterResource(id = R.drawable.default_pfp_light)
        )
    }
}
