package com.example.sajisehat.feature.trek.save

import androidx.compose.ui.graphics.Color

// LEVEL GULA PRODUK (per 1 takaran saji)
enum class ProductSugarLevelUi {
    LOW,
    MEDIUM,
    HIGH
}

private const val DAILY_LIMIT_GRAM = 50.0   // masih pakai 50 gr/hari buat hitung %

fun getProductSugarLevel(perServingGram: Double): ProductSugarLevelUi =
    when {
        perServingGram <= 5.0  -> ProductSugarLevelUi.LOW
        perServingGram <= 15.0 -> ProductSugarLevelUi.MEDIUM
        else                   -> ProductSugarLevelUi.HIGH
    }

// teks judul di card produk
fun ProductSugarLevelUi.titleText(): String = when (this) {
    ProductSugarLevelUi.LOW -> "Rendah"
    ProductSugarLevelUi.MEDIUM -> "Sedang"
    ProductSugarLevelUi.HIGH -> "Tinggi"
}

// warna accent untuk teks persen, dsb
fun ProductSugarLevelUi.accentColor(): Color = when (this) {
    ProductSugarLevelUi.LOW -> Color(0xFF0047BA)
    ProductSugarLevelUi.MEDIUM -> Color(0xFFFFA726)
    ProductSugarLevelUi.HIGH -> Color(0xFFE53935)
}

// persen kebutuhan harian yang diwakili 1 saji produk
fun calculateProductPercent(perServingGram: Double): Int {
    val percent = (perServingGram / DAILY_LIMIT_GRAM) * 100.0
    return percent.toInt().coerceAtLeast(0)
}
