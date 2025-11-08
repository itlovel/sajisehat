package com.example.sajisehat.feature.trek.save

data class SaveTrekUiState(
    val sugarGram: Double = 0.0,       // gula produk baru
    val totalBefore: Double = 0.0,     // total gula hari ini sebelum produk ini
    val totalAfter: Double = 0.0,      // total gula hari ini setelah produk ini
    val productName: String = "",      // input user
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false       // true kalau sudah berhasil simpan
)
