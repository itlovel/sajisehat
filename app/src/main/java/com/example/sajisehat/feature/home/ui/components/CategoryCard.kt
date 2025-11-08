package com.example.sajisehat.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles

enum class CategoryKind { AddManual, ReadArticle, WatchVideo }

@Composable
fun CategoryCard(
    onAddManual: () -> Unit,
    onReadArticle: () -> Unit,
    onWatchVideo: () -> Unit,
    modifier: Modifier = Modifier,
    initialSelected: CategoryKind? = null
) {
    var selected by rememberSaveable { mutableStateOf(initialSelected) }


    val screenW = LocalConfiguration.current.screenWidthDp.dp
    val circleSize = (screenW * 0.13f).coerceIn(44.dp, 64.dp)
    val iconSize   = (circleSize * 0.48f).coerceIn(20.dp, 32.dp)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Kategori:",
                style = SajiTextStyles.BodyLargeSemibold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryButton(
                    icon = R.drawable.ic_add_manual,
                    label = "Tambah Manual",
                    selected = selected == CategoryKind.AddManual,
                    circleSize = circleSize,
                    iconSize = iconSize,
                    onClick = {
                        selected = CategoryKind.AddManual
                        onAddManual()
                    },
                    modifier = Modifier.weight(1f)
                )
                CategoryButton(
                    icon = R.drawable.ic_read_article,
                    label = "Baca Artikel",
                    selected = selected == CategoryKind.ReadArticle,
                    circleSize = circleSize,
                    iconSize = iconSize,
                    onClick = {
                        selected = CategoryKind.ReadArticle
                        onReadArticle()
                    },
                    modifier = Modifier.weight(1f)
                )
                CategoryButton(
                    icon = R.drawable.ic_watch_video,
                    label = "Nonton Video",
                    selected = selected == CategoryKind.WatchVideo,
                    circleSize = circleSize,
                    iconSize = iconSize,
                    onClick = {
                        selected = CategoryKind.WatchVideo
                        onWatchVideo()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CategoryButton(
    icon: Int,
    label: String,
    selected: Boolean,
    circleSize: Dp,
    iconSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary  = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val accent   = MaterialTheme.colorScheme.secondary

    val circleBg = if (selected) accent else onPrimary
    val iconTint = primary
    val textColor = if (selected) accent else onPrimary

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = SajiTextStyles.CaptionBold,
            color = textColor
        )
    }
}
