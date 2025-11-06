package com.example.sajisehat.ui.components.bottombar

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int,
    @DrawableRes val iconSelectedRes: Int = iconRes,
    val useTint: Boolean = false // true kalau ikon monokrom & ingin ikut warna tema
)

