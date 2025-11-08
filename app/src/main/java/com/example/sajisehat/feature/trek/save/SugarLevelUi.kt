package com.example.sajisehat.feature.trek.save

import androidx.compose.ui.graphics.Color

// Mapping dari Figma:
// Rendah: 0–25g, Sedang: 26–50g, Tinggi: >50g (per hari)
enum class SugarLevelUi {
    LOW, MEDIUM, HIGH
}

private const val DAILY_LIMIT_GRAM = 50.0

fun getSugarLevelUi(totalSugarGram: Double): SugarLevelUi {
    return when {
        totalSugarGram <= 25.0 -> SugarLevelUi.LOW
        totalSugarGram <= 50.0 -> SugarLevelUi.MEDIUM
        else -> SugarLevelUi.HIGH
    }
}

fun SugarLevelUi.titleText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Tinggi"
}

fun SugarLevelUi.accentColor(): Color = when (this) {
    SugarLevelUi.LOW -> Color(0xFF0047BA)      // biru terang
    SugarLevelUi.MEDIUM -> Color(0xFFFFA726)   // oranye
    SugarLevelUi.HIGH -> Color(0xFFE53935)     // merah
}

fun calculateDailyPercent(totalSugarGram: Double): Int {
    val percent = (totalSugarGram / DAILY_LIMIT_GRAM) * 100.0
    return percent.toInt().coerceAtLeast(0)
}
