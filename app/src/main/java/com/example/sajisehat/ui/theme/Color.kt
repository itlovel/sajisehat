package com.example.sajisehat.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme


// ---- Brand tokens (literal colors from your design system)
val BrandNavy      = Color(0xFF000957) // TODO: replace with your navy
val Another = Color(0xFF545A8E)
val BrandYellow    = Color(0xFFFFB200) // TODO: replace with your yellow
val BrandCream     = Color(0xFFF6F6FA) // light background neutral (optional)
val Neutral10      = Color(0xFF0F1115) // darkest
val Neutral20      = Color(0xFF1B1E24)
val Neutral30      = Color(0xFF2A2F37)
val Neutral40      = Color(0xFF3C424D)
val Neutral60      = Color(0xFF7B8394)
val Neutral80      = Color(0xFFB9C0CF)
val Neutral95      = Color(0xFFF0F1F5)

// State tokens (adjust to your DS)
val Success = Color(0xFF2E7D32)
val Warning = Color(0xFFF9A825)
val Error   = Color(0xFFB00020)

// ---- Material 3 semantic mapping
val LightColors = lightColorScheme(
    primary        = BrandNavy,
    onPrimary      = Color.White,
    primaryContainer = BrandNavy,     // navy surfaces (you can create a lighter navy if needed)
    onPrimaryContainer = Color.White,

    Another,


    secondary      = BrandYellow,
    onSecondary    = Color(0xFF1B1B1B),
    secondaryContainer = BrandYellow,
    onSecondaryContainer = Color(0xFF1B1B1B),

    background     = BrandCream,      // light page bg
    onBackground   = Neutral10,

    surface        = Color.White,     // cards/sheets
    onSurface      = Neutral20,

    outline        = Neutral80,
    error          = Error,
    onError        = Color.White
)

val DarkColors = darkColorScheme(
    primary        = BrandYellow,     // flip accent for contrast in dark
    onPrimary      = Neutral20,
    primaryContainer = BrandYellow,
    onPrimaryContainer = Neutral20,

    secondary      = BrandNavy,
    onSecondary    = Color.White,


    background     = Neutral10,
    onBackground   = Color(0xFFECECEC),

    surface        = Neutral20,
    onSurface      = Color(0xFFECECEC),

    outline        = Neutral60,
    error          = Error,
    onError        = Color.White
)
