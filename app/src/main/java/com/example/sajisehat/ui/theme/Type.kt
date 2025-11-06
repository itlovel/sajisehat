package com.example.sajisehat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.sajisehat.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val roboto = GoogleFont("Roboto")

private val RobotoFamily = FontFamily(
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Light),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Normal),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Medium),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.SemiBold),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Bold),
)

val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge    = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Medium,   fontSize = 20.sp, lineHeight = 28.sp),
    bodyLarge     = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge    = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
)
