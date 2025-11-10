package com.example.sajisehat.data.scan

import android.net.Uri
import com.example.sajisehat.data.scan.model.NutritionScanResult

/**
 * Abstraksi utama untuk fitur Scan.
 *
 * UI akan:
 * 1) Memanggil ML Kit Document Scanner → dapat list Uri gambar halaman label.
 * 2) Mengirim list Uri itu ke ScanRepository.
 *
 * Repository akan:
 * - Membaca teks dari gambar (OCR).
 * - Parsing teks jadi NutritionScanResult.
 */
interface ScanRepository {

    /**
     * Proses satu atau beberapa halaman hasil scan label.
     *
     * @param imageUris daftar Uri gambar hasil ML Kit Document Scanner (JPEG).
     */
    suspend fun processScannedImages(imageUris: List<Uri>): Result<NutritionScanResult>
}
