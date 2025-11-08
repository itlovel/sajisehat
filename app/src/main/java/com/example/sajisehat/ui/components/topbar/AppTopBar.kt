package com.example.sajisehat.ui.components.topbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sajisehat.feature.topbar.TopBarEvent
import com.example.sajisehat.feature.topbar.TopBarState
import com.example.sajisehat.ui.theme.SajiTextStyles


@Composable
fun AppTopBar(
    state: TopBarState,
    onEvent: (TopBarEvent) -> Unit,
    modifier: Modifier = Modifier,
    customTitle: String? = null,
    customSubtitle: String? = null
) {
    // Resolusi final text, supaya tetap backward-compatible
    val titleText = customTitle ?: "Hi ${state.greetingName}!"
    val subtitleText = customSubtitle ?: state.subtitle

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),     // hindari notch
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back (opsional)
            if (state.showBack) {
                IconButton(onClick = { onEvent(TopBarEvent.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(4.dp))
            }

            // Title area (tetap sama layout & ukuran)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = titleText,
                    style = SajiTextStyles.BodyLargeSemibold,
                    color = MaterialTheme.colorScheme.background
                )
                subtitleText?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = SajiTextStyles.Body,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                    )
                }
            }

            // Actions: bell + avatar
            BadgedBox(
                badge = {
                    if (state.unreadCount > 0) {
                        Badge {
                            Text(state.unreadCount.coerceAtMost(9).toString())
                        }
                    }
                }
            ) {
                IconButton(onClick = { onEvent(TopBarEvent.OnBellClick) }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = { onEvent(TopBarEvent.OnAvatarClick) }) {
                val border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                )
                if (state.avatarUrl.isNullOrBlank()) {
                    // placeholder avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            .border(border, CircleShape)
                    )
                } else {
                    AsyncImage(
                        model = state.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(border, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
