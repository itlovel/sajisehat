// com/example/sajisehat/data/scan/DocumentScannerDataSource.kt
package com.example.sajisehat.data.scan

import android.content.Context
import android.net.Uri

/**
 * Abstraksi data source untuk membaca teks dari dokumen hasil scan.
 *
 * Catatan penting:
 * - ML Kit Document Scanner memberikan gambar (JPEG/PDF), bukan teks.
 * - Di sini kita definisikan kontrak "image(s) → text" (OCR).
 * - Implementasi konkretnya akan memakai ML Kit Text Recognition.
 */
interface DocumentScannerDataSource {

    /**
     * Jalankan OCR terhadap satu atau beberapa halaman hasil scan,
     * dan gabungkan semua teks menjadi satu string.
     */
    suspend fun readTextFromDocumentPages(
        context: Context,
        imageUris: List<Uri>
    ): String
}
