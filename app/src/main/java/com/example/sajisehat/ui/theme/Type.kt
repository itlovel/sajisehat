// ui/theme/Type.kt
package com.example.sajisehat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.sajisehat.R

// --- Google Fonts Roboto provider ---
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage  = "com.google.android.gms",
    certificates     = R.array.com_google_android_gms_fonts_certs
)

private val roboto = GoogleFont("Roboto")

// Di Figma hanya ada 3 berat yang dipakai: Regular (400), SemiBold (600), Bold (700)
val RobotoFamily: FontFamily = FontFamily(
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Normal),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.SemiBold),
    androidx.compose.ui.text.googlefonts.Font(roboto, provider, FontWeight.Bold),
)

/**
 * Semua style persis seperti di Figma.
 * LineHeight ≈ 120% dari fontSize.
 */
object SajiTextStyles {

    // ------- H1 (61px / 120%) -------
    val H1 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 61.sp,
        lineHeight = 73.sp
    )
    val H1Semibold = H1.copy(fontWeight = FontWeight.SemiBold)
    val H1Bold     = H1.copy(fontWeight = FontWeight.Bold)

    // ------- H2 (49px / 120%) -------
    val H2 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 49.sp,
        lineHeight = 59.sp
    )
    val H2Semibold = H2.copy(fontWeight = FontWeight.SemiBold)
    val H2Bold     = H2.copy(fontWeight = FontWeight.Bold)

    // ------- H3 (39px / 120%) -------
    val H3 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 39.sp,
        lineHeight = 47.sp
    )
    val H3Semibold = H3.copy(fontWeight = FontWeight.SemiBold)
    val H3Bold     = H3.copy(fontWeight = FontWeight.Bold)

    // ------- H4 (31px / 120%) -------
    val H4 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 31.sp,
        lineHeight = 37.sp
    )
    val H4Semibold = H4.copy(fontWeight = FontWeight.SemiBold)
    val H4Bold     = H4.copy(fontWeight = FontWeight.Bold)

    // ------- H5 (25px / 120%) -------
    val H5 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 30.sp
    )
    val H5Semibold = H5.copy(fontWeight = FontWeight.SemiBold)
    val H5Bold     = H5.copy(fontWeight = FontWeight.Bold)

    // ------- Body Large (20px / 120%) -------
    val BodyLarge = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
    val BodyLargeSemibold = BodyLarge.copy(fontWeight = FontWeight.SemiBold)
    val BodyLargeBold     = BodyLarge.copy(fontWeight = FontWeight.Bold)

    // ------- Body (16px / 120%) -------
    val Body = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 19.sp
    )
    val BodySemibold = Body.copy(fontWeight = FontWeight.SemiBold)
    val BodyBold     = Body.copy(fontWeight = FontWeight.Bold)

    // ------- Caption (13px / 120%) -------
    val Caption = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 16.sp
    )
    val CaptionSemibold = Caption.copy(fontWeight = FontWeight.SemiBold)
    val CaptionBold     = Caption.copy(fontWeight = FontWeight.Bold)
}

/**
 * Material3 Typography default.
 * Ini hanya *mapping* ke style Figma di atas supaya
 * semua pemanggilan `MaterialTheme.typography.*`
 * tetap bekerja tanpa kamu ubah satu-satu.
 */
val AppTypography = Typography(
    // Biasanya dipakai judul paling besar (jarang dipakai di mobile)
    displayLarge  = SajiTextStyles.H1,
    displayMedium = SajiTextStyles.H2,
    displaySmall  = SajiTextStyles.H3,

    // Heading lain
    headlineLarge  = SajiTextStyles.H4,
    headlineMedium = SajiTextStyles.H5,
    headlineSmall  = SajiTextStyles.BodyLargeSemibold, // bisa untuk sub-title gede

    // Title (misalnya judul section, dll)
    titleLarge  = SajiTextStyles.BodyLarge,
    titleMedium = SajiTextStyles.BodySemibold,
    titleSmall  = SajiTextStyles.CaptionBold,

    // Body text
    bodyLarge  = SajiTextStyles.BodyLarge,
    bodyMedium = SajiTextStyles.Body,
    bodySmall  = SajiTextStyles.Caption,

    // Label (button, pill, dsb)
    labelLarge  = SajiTextStyles.BodyLargeSemibold,
    labelMedium = SajiTextStyles.CaptionSemibold,
    labelSmall  = SajiTextStyles.Caption
)
