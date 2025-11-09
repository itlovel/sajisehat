package com.example.sajisehat.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sajisehat.R
import com.example.sajisehat.feature.topbar.TopBarEvent
import com.example.sajisehat.feature.topbar.TopBarViewModel
import com.example.sajisehat.ui.components.topbar.AppTopBar
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun ProfileScreen(
    onGoSettingsMarkah: () -> Unit = {},
    onGoNotificationSettings: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    topBarVM: TopBarViewModel = viewModel(),
    vm: ProfileViewModel = viewModel()
) {
    val st by vm.state.collectAsState()

    LaunchedEffect(st.photoUrl, st.displayName) {
        topBarVM.setUser(
            name = st.displayName.ifBlank { "Kamu" },
            avatarUrl = st.photoUrl
        )
    }

    val screenW = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenW * 0.70f
    val avatarSize = 88.dp

    Scaffold(
        topBar = {
            AppTopBar(
                state = topBarVM.state.collectAsState().value,
                onEvent = { evt ->
                    when (evt) {
                        TopBarEvent.OnAvatarClick -> {
                        }
                        else -> topBarVM.onEvent(evt)
                    }
                },
                customTitle = "Profil",
                customSubtitle = "Cek Informasi Dirimu"
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .width(cardWidth)
                        .padding(top = avatarSize / 2)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = avatarSize / 2 + 12.dp,
                                bottom = 20.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = st.displayName.ifBlank { "Pengguna" },
                            style = SajiTextStyles.BodyLargeBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = st.email.ifBlank { "-" },
                            style = SajiTextStyles.BodyLargeBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .size(avatarSize)
                        .align(Alignment.TopCenter)
                ) {
                    if (st.photoUrl.isNullOrBlank()) {
                        Image(
                            painter = painterResource(R.drawable.ic_profile_placeholder),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = st.photoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            SettingRow(title = "Markah", onClick = onGoSettingsMarkah)
            Spacer(Modifier.height(20.dp))
            SettingRow(title = "Notifikasi", onClick = onGoNotificationSettings)

            Spacer(Modifier.height(100.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SAJISEHAT Ver. 1.0",
                        style = SajiTextStyles.BodyBold
                    )
                    Text(
                        text = "Baca Labelnya, Jaga Gula-nya, Sehat Raganya!",
                        style = SajiTextStyles.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { vm.logout(onLoggedOut) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Log Out",
                    style = SajiTextStyles.BodyLargeBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = SajiTextStyles.BodySemibold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
