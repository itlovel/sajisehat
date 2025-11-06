package com.example.sajisehat.ui.components.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BottomBarSpec(
    val barHeight: Dp,
    val iconSize: Dp,
    val fabSize: Dp,
    val fabIconSize: Dp,
    val fabOffsetY: Dp,
    val haloSize: Dp,
    val spacerWidth: Dp,
    val showLabels: Boolean
)

@Composable
fun rememberBottomBarSpec(): BottomBarSpec {
    val cfg = LocalConfiguration.current
    val widthDp = cfg.screenWidthDp
    val fontScale = LocalDensity.current.fontScale.coerceIn(1f, 1.25f)

    return when {
        widthDp < 360 -> BottomBarSpec(
            barHeight   = (62f * fontScale).dp,
            iconSize    = 24.dp,
            fabSize     = 52.dp,
            fabIconSize = 24.dp,
            fabOffsetY  = 24.dp,   // ↓ lebih turun
            haloSize    = 56.dp,
            spacerWidth = 70.dp,
            showLabels  = true
        )
        widthDp < 400 -> BottomBarSpec(
            barHeight   = (68f * fontScale).dp,
            iconSize    = 28.dp,
            fabSize     = 56.dp,
            fabIconSize = 26.dp,
            fabOffsetY  = 28.dp,   // ↓ lebih turun lagi
            haloSize    = 60.dp,
            spacerWidth = 76.dp,
            showLabels  = true
        )
        else -> BottomBarSpec(
            barHeight   = (72f * fontScale).dp,
            iconSize    = 30.dp,
            fabSize     = 60.dp,
            fabIconSize = 28.dp,
            fabOffsetY  = 30.dp,   // ↓
            haloSize    = 64.dp,
            spacerWidth = 82.dp,
            showLabels  = true
        )
    }
}
