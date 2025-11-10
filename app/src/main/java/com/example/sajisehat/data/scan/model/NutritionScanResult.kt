package com.example.sajisehat.data.scan.model

data class NutritionScanResult(
    val rawText: String,               // teks mentah dari label
    val productName: String? = null,   // nama produk (jika bisa diambil)
    val servingSizeGram: Double? = null,      // takaran saji per 1x sajian (gram)
    val servingsPerPack: Int? = null,         // jumlah sajian per kemasan
    val sugarPerServingGram: Double? = null,  // gula per 1x sajian (gram)
    val sugarPerPackGram: Double? = null      // gula total per kemasan (gram)
)
