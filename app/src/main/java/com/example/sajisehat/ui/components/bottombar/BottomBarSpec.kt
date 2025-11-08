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
        // HP kecil (≤ 360dp)
        widthDp < 360 -> BottomBarSpec(
            barHeight   = (60f * fontScale).dp,
            iconSize    = 15.dp,   // dulu 24
            fabSize     = 40.dp,   // dulu 52
            fabIconSize = 25.dp,   // dulu 24
            fabOffsetY  = 20.dp,
            haloSize    = 54.dp,
            spacerWidth = 70.dp,
            showLabels  = true
        )

        // HP sedang (360–399dp)
        widthDp < 400 -> BottomBarSpec(
            barHeight   = (66f * fontScale).dp,
            iconSize    = 17.dp,   // dulu 28
            fabSize     = 44.dp,   // dulu 56
            fabIconSize = 27.dp,   // dulu 26
            fabOffsetY  = 24.dp,
            haloSize    = 58.dp,
            spacerWidth = 76.dp,
            showLabels  = true
        )

        // HP lebar (≥ 400dp)
        else -> BottomBarSpec(
            barHeight   = (70f * fontScale).dp,
            iconSize    = 19.dp,   // dulu 30
            fabSize     = 48.dp,   // dulu 60
            fabIconSize = 29.dp,   // dulu 28
            fabOffsetY  = 26.dp,
            haloSize    = 62.dp,
            spacerWidth = 82.dp,
            showLabels  = true
        )
    }
}

