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

    private fun expandRectToMinSize(
        rect: android.graphics.Rect,
        minSize: Int,
        imageWidth: Int,
        imageHeight: Int
    ): android.graphics.Rect {

        val cx = rect.centerX()
        val cy = rect.centerY()

        val half = minSize / 2

        val newLeft = (cx - half).coerceAtLeast(0)
        val newTop = (cy - half).coerceAtLeast(0)
        val newRight = (cx + half).coerceAtMost(imageWidth)
        val newBottom = (cy + half).coerceAtMost(imageHeight)

        return android.graphics.Rect(newLeft, newTop, newRight, newBottom)
    }


    private suspend fun ocrAllDetections(
        fullBitmap: Bitmap,
        layout: LayoutDetectionResult
    ): List<OcrSegment> {
        val result = mutableListOf<OcrSegment>()
        val imageWidth = layout.imageWidth
        val imageHeight = layout.imageHeight

        val minSize = 32
        val margin = 8 // px, biar nggak terlalu ketat

        layout.detections.forEach { det ->
            // 1. Rect awal dari Roboflow
            val baseRect = det.toRect(imageWidth, imageHeight)

            if (baseRect.width() <= 0 || baseRect.height() <= 0) return@forEach

            // 2. Perbesar sedikit (kasih margin ke segala arah)
            val expandedRect = android.graphics.Rect(
                (baseRect.left - margin).coerceAtLeast(0),
                (baseRect.top - margin).coerceAtLeast(0),
                (baseRect.right + margin).coerceAtMost(imageWidth),
                (baseRect.bottom + margin).coerceAtMost(imageHeight)
            )

            if (expandedRect.width() <= 0 || expandedRect.height() <= 0) return@forEach

            // 3. Crop dari bitmap asli
            val crop = Bitmap.createBitmap(
                fullBitmap,
                expandedRect.left,
                expandedRect.top,
                expandedRect.width(),
                expandedRect.height()
            )

            // 4. Kalau crop masih terlalu kecil untuk MLKit, upscale dulu
            val ocrBitmap: Bitmap = if (crop.width < minSize || crop.height < minSize) {
                val newWidth = maxOf(crop.width, minSize)
                val newHeight = maxOf(crop.height, minSize)
                Bitmap.createScaledBitmap(crop, newWidth, newHeight, true)
            } else {
                crop
            }

            // 5. Baru kirim ke MLKit Text Recognizer
            val text = textRecognizer.recognizeText(ocrBitmap)
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
