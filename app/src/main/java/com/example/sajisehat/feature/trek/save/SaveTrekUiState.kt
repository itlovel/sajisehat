package com.example.sajisehat.feature.trek.save

data class SaveTrekUiState(
    val sugarGram: Double = 0.0,
    val totalBefore: Double = 0.0,
    val totalAfter: Double = 0.0,
    val productName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
