package com.example.sajisehat.data.scan

import android.net.Uri
import com.example.sajisehat.data.scan.model.NutritionScanResult

interface ScanRepository {
    suspend fun processScannedImages(
        imageUris: List<Uri>
    ): Result<NutritionScanResult>
}
