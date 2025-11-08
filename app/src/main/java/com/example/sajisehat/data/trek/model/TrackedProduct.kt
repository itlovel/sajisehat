package com.example.sajisehat.data.trek.model

data class TrackedProduct(
    val id: String = "",          // akan diisi docId Firestore
    val userId: String = "",
    val productName: String = "",
    val sugarGram: Double = 0.0,
    val scannedAt: Long = 0L,     // System.currentTimeMillis()
    val date: String = ""         // format "yyyy-MM-dd"
)
