// com/example/sajisehat/data/scan/ScanRepositoryImpl.kt
package com.example.sajisehat.data.scan

import android.content.Context
import android.net.Uri
import com.example.sajisehat.data.scan.model.NutritionScanResult

/**
 * Implementasi ScanRepository.
 *
 * Tergantung pada:
 * - DocumentScannerDataSource → untuk membaca teks (OCR) dari gambar.
 * - NutritionLabelParser      → untuk mem-parsing teks label gizi.
 */
class ScanRepositoryImpl(
    private val appContext: Context,
    private val documentScannerDataSource: DocumentScannerDataSource,
    private val nutritionLabelParser: NutritionLabelParser
) : ScanRepository {

    override suspend fun processScannedImages(
        imageUris: List<Uri>
    ): Result<NutritionScanResult> {
        return try {
            // 1. OCR: gabungkan teks dari semua halaman
            val rawText = documentScannerDataSource.readTextFromDocumentPages(
                context = appContext,
                imageUris = imageUris
            )

            // 2. Parsing label gizi
            val parsed = nutritionLabelParser.parse(rawText)

            Result.success(parsed)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
