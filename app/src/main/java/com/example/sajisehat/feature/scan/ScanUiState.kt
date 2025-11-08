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

data class ScanResultUi(
    val productName: String? = null,
    val servingSizeGram: Double? = null,
    val servingsPerPack: Int? = null,
    val sugarPerServingGram: Double? = null,
    val sugarPerPackGram: Double? = null,
    val dailyPercent: Int? = null,
    val sugarLevel: SugarLevel? = null,
    val rawText: String? = null
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
