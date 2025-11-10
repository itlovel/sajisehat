// feature/scan/ScanUiState.kt
package com.example.sajisehat.feature.scan

import com.example.sajisehat.data.scan.model.NutritionScanResult

enum class ScanStep {
    PERMISSION,
    SCANNING,
    PROCESSING,
    RESULT
}

enum class CameraPermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

enum class SugarLevel {
    RENDAH, SEDANG, TINGGI
}

/**
 * Hasil scan yang sudah “siap tampil” di UI.
 * (nama produk nanti user isi sendiri di screen berikutnya)
 */
data class ScanResultUi(
    val productName: String? = null,          // sekarang belum dipakai di UI
    val servingSizeGram: Double? = null,      // takaran saji (gram)
    val servingsPerPack: Int? = null,         // jumlah sajian per kemasan
    val sugarPerServingGram: Double? = null,  // gula per 1 takaran saji
    val sugarPerPackGram: Double? = null,     // gula total per kemasan (optional)
    val dailyPercent: Int? = null,            // % kebutuhan gula harian
    val sugarLevel: SugarLevel? = null,       // Rendah / Sedang / Tinggi
    val rawText: String? = null               // teks OCR (buat debug)
)

/**
 * State utama untuk fitur Scan.
 */
data class ScanUiState(
    val step: ScanStep = ScanStep.PERMISSION,
    val permissionStatus: CameraPermissionStatus = CameraPermissionStatus.UNKNOWN,

    val isProcessing: Boolean = false,
    val errorMessage: String? = null,

    val lastResult: ScanResultUi? = null,
    val isExpandedInfo: Boolean = false
)
