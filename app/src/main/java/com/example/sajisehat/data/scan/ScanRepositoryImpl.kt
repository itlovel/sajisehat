package com.example.sajisehat.data.scan

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.sajisehat.data.scan.model.NutritionRawSegments
import com.example.sajisehat.data.scan.model.NutritionScanResult
import com.example.sajisehat.data.scan.model.toRect
import com.example.sajisehat.data.scan.remote.LayoutDetectionResult
import com.example.sajisehat.data.scan.remote.ScanNutritionRemoteDataSource
import com.example.sajisehat.data.scan.remote.toLayoutDetectionResult
import com.example.sajisehat.data.scan.ocr.TextRecognitionDataSource
import java.io.ByteArrayOutputStream

class ScanRepositoryImpl(
    private val appContext: Context,
    private val layoutRemote: ScanNutritionRemoteDataSource,
    private val textRecognizer: TextRecognitionDataSource,
    private val nutritionLabelParser: NutritionLabelParser
) : ScanRepository {

    override suspend fun processScannedImages(
        imageUris: List<Uri>
    ): Result<NutritionScanResult> {
        return try {
            if (imageUris.isEmpty()) {
                return Result.failure(IllegalArgumentException("Tidak ada gambar yang dipindai"))
            }

            // 1. Ambil hanya halaman pertama dulu
            val firstUri = imageUris.first()
            val bitmap = loadBitmapFromUri(firstUri)
            val bytes = bitmap.toJpegBytes()

            // 2. Kirim ke backend → dapat layout
            val response = layoutRemote.detectLayout(bytes)
            if (!response.success || response.data == null) {
                return Result.failure(IllegalStateException(response.message ?: "Gagal mendeteksi layout"))
            }

            val layout: LayoutDetectionResult = response.toLayoutDetectionResult()

            // 3. OCR setiap detection (bbox)
            val ocrSegments = ocrAllDetections(
                fullBitmap = bitmap,
                layout = layout
            )

            // 4. Resolve → segmen tekstual per class
            val rawSegments = resolveSegments(ocrSegments)

            // 5. Parse → NutritionScanResult (logic lama)
            val parsed = nutritionLabelParser.parse(rawSegments)

            Result.success(parsed)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    // --- helper internal ---

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val resolver = appContext.contentResolver
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = android.graphics.ImageDecoder.createSource(resolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            android.provider.MediaStore.Images.Media.getBitmap(resolver, uri)
        }
    }

    private fun Bitmap.toJpegBytes(quality: Int = 95): ByteArray {
        val bos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        return bos.toByteArray()
    }

    private suspend fun ocrAllDetections(
        fullBitmap: Bitmap,
        layout: LayoutDetectionResult
    ): List<OcrSegment> {
        val result = mutableListOf<OcrSegment>()
        val imageWidth = layout.imageWidth
        val imageHeight = layout.imageHeight

        layout.detections.forEach { det ->
            val rect = det.toRect(imageWidth, imageHeight)
            if (rect.width() <= 0 || rect.height() <= 0) return@forEach

            val crop = Bitmap.createBitmap(
                fullBitmap,
                rect.left,
                rect.top,
                rect.width(),
                rect.height()
            )

            val text = textRecognizer.recognizeText(crop)
            if (text.isNotBlank()) {
                result += OcrSegment(
                    clazz = det.clazz,
                    text = text,
                    confidence = det.confidence
                )
            }
        }

        return result
    }

    private fun resolveSegments(segments: List<OcrSegment>): NutritionRawSegments {
        fun bestFor(clazz: String, pattern: Regex? = null): String? {
            val candidates = segments
                .filter { it.clazz == clazz }
                .sortedByDescending { it.confidence }

            if (candidates.isEmpty()) return null

            pattern?.let { regex ->
                candidates.firstOrNull { regex.containsMatchIn(it.text) }?.let { return it.text }
            }

            return candidates.first().text
        }

        val takaranSajiPattern =
            Regex("""\d+(\.\d+)?\s*(g|ml|sendok|sdt|sdm)""", RegexOption.IGNORE_CASE)
        val gulaPattern =
            Regex("""\d+(\.\d+)?\s*g""", RegexOption.IGNORE_CASE)
        val sajianPerKemasanPattern =
            Regex("""\d+\s*(sajian|serving)""", RegexOption.IGNORE_CASE)

        return NutritionRawSegments(
            takaranSaji = bestFor("takaran_saji", takaranSajiPattern),
            sajianPerKemasan = bestFor("sajian_per_kemasan", sajianPerKemasanPattern),
            gula = bestFor("gula", gulaPattern)
        )
    }

    private data class OcrSegment(
        val clazz: String,
        val text: String,
        val confidence: Float
    )
}
