package com.example.sajisehat.data.scan

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.math.max

/**
 * Implementasi DocumentScannerDataSource berbasis ML Kit.
 *
 * - Menerima list Uri gambar hasil ML Kit Document Scanner.
 * - Untuk tiap halaman:
 *   1) Load bitmap dengan ukuran aman.
 *   2) Terapkan orientasi EXIF jika ada.
 *   3) Enhance untuk OCR (grayscale + kontras + unsharp).
 *   4) Jalankan OCR ke bitmap enhanced.
 *   5) Jika hasil kosong, coba OCR versi adaptive binarize.
 * - Teks dari semua halaman digabung menjadi satu string.
 */
class MlKitDocumentScannerDataSource : DocumentScannerDataSource {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun readTextFromDocumentPages(
        context: Context,
        imageUris: List<Uri>
    ): String {
        if (imageUris.isEmpty()) return ""

        val sb = StringBuilder()

        for ((index, uri) in imageUris.withIndex()) {
            try {
                val base = loadBitmapSafely(context, uri)
                val oriented = applyExifOrientationIfAny(context, base, uri)
                val enhanced = enhanceForOcr(oriented)
                val bin = adaptiveBinarize(enhanced)

                // OCR pada gambar enhanced terlebih dahulu
                val text1 = runOcr(enhanced)
                val finalText = if (text1.isBlank()) runOcr(bin) else text1

                if (finalText.isNotBlank()) {
                    sb.appendLine("===== PAGE ${index + 1} =====")
                    sb.appendLine(finalText)
                }
            } catch (t: Throwable) {
                Log.e("MlKitDocScannerDS", "Failed processing uri=$uri: ${t.message}", t)
            }
        }

        return sb.toString().trim()
    }

    // ================= OCR =================

    private suspend fun runOcr(bitmap: Bitmap): String =
        suspendCancellableCoroutine { cont ->
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(image)
                    .addOnSuccessListener { vt ->
                        cont.resume(vt.text ?: "")
                    }
                    .addOnFailureListener { e ->
                        Log.e("MlKitDocScannerDS", "OCR failure: ${e.message}", e)
                        cont.resume("")
                    }
            } catch (e: Exception) {
                Log.e("MlKitDocScannerDS", "OCR exception: ${e.message}", e)
                cont.resume("")
            }
        }

    // ================= IMAGE LOADING =================

    private fun loadBitmapSafely(
        context: Context,
        uri: Uri,
        maxSide: Int = 2000
    ): Bitmap {
        val resolver = context.contentResolver

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(resolver, uri)
            val bmp = ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
            scaleDownIfNeeded(bmp, maxSide)
        } else {
            // 1) Baca bounds dulu
            resolver.openInputStream(uri).use { input ->
                requireNotNull(input)
                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(input, null, opts)
            }

            // 2) Hitung sample size
            val opts2 = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
                inSampleSize = calcInSampleSize(context, uri, maxSide)
            }

            resolver.openInputStream(uri).use { input2 ->
                requireNotNull(input2)
                BitmapFactory.decodeStream(input2, null, opts2)!!
            }
        }
    }

    private fun scaleDownIfNeeded(src: Bitmap, maxSide: Int): Bitmap {
        val w = src.width
        val h = src.height
        val maxCur = max(w, h)
        if (maxCur <= maxSide) return src

        val r = maxSide.toFloat() / maxCur.toFloat()
        return Bitmap.createScaledBitmap(
            src,
            (w * r).toInt(),
            (h * r).toInt(),
            true
        )
    }

    private fun calcInSampleSize(
        context: Context,
        uri: Uri,
        maxSide: Int
    ): Int {
        val resolver = context.contentResolver
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, opts)
        }

        var sample = 1
        val maxCur = max(opts.outWidth, opts.outHeight)
        while (maxCur / sample > maxSide) {
            sample *= 2
        }
        return sample
    }

    // ================= EXIF ORIENTATION =================

    private fun applyExifOrientationIfAny(
        context: Context,
        src: Bitmap,
        uri: Uri
    ): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    else -> return src
                }
                Bitmap.createBitmap(
                    src, 0, 0, src.width, src.height, matrix, true
                )
            } ?: src
        } catch (e: IOException) {
            Log.w("MlKitDocScannerDS", "EXIF read failed: ${e.message}")
            src
        } catch (e: Exception) {
            src
        }
    }

    // ================= IMAGE ENHANCEMENT =================

    private fun enhanceForOcr(src: Bitmap): Bitmap {
        // 1) scale up sedikit
        val scaleFactor = 1.5f
        val scaled = Bitmap.createScaledBitmap(
            src,
            (src.width * scaleFactor).toInt(),
            (src.height * scaleFactor).toInt(),
            true
        )

        // 2) grayscale + contrast
        val grayMatrix = ColorMatrix().apply { setSaturation(0f) }

        val contrast = 1.4f
        val translate = (-0.5f * contrast + 0.5f) * 255
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )

        grayMatrix.postConcat(contrastMatrix)

        val contrasted = Bitmap.createBitmap(
            scaled.width,
            scaled.height,
            Bitmap.Config.ARGB_8888
        )

        Canvas(contrasted).drawBitmap(
            scaled,
            0f,
            0f,
            Paint().apply {
                colorFilter = ColorMatrixColorFilter(grayMatrix)
            }
        )

        // 3) unsharp mask ringan untuk menajamkan teks
        return unsharp(contrasted)
    }

    private fun unsharp(src: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(
                ColorMatrix(
                    floatArrayOf(
                        0f, -1f, 0f, 0f, 0f,
                        -1f, 5f, -1f, 0f, 0f,
                        0f, -1f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }
        Canvas(out).drawBitmap(src, 0f, 0f, paint)
        return out
    }

    // ================= ADAPTIVE BINARIZATION =================

    private fun adaptiveBinarize(
        src: Bitmap,
        win: Int = 31,
        offset: Int = 10
    ): Bitmap {
        val w = src.width
        val h = src.height

        val argb = IntArray(w * h)
        src.getPixels(argb, 0, w, 0, 0, w, h)

        // konversi ke grayscale
        val gray = IntArray(w * h)
        for (i in argb.indices) {
            val p = argb[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            gray[i] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        // summed-area table untuk mean lokal cepat
        val sat = LongArray((w + 1) * (h + 1))
        fun idxSAT(x: Int, y: Int) = y * (w + 1) + x

        for (y in 1..h) {
            var rowSum = 0L
            for (x in 1..w) {
                rowSum += gray[(y - 1) * w + (x - 1)]
                sat[idxSAT(x, y)] = sat[idxSAT(x, y - 1)] + rowSum
            }
        }

        val half = win / 2
        val out = IntArray(w * h)

        fun sumRect(x0: Int, y0: Int, x1: Int, y1: Int): Long {
            val xa = x0.coerceIn(0, w)
            val ya = y0.coerceIn(0, h)
            val xb = x1.coerceIn(0, w)
            val yb = y1.coerceIn(0, h)
            return sat[idxSAT(xb, yb)] -
                    sat[idxSAT(xa, yb)] -
                    sat[idxSAT(xb, ya)] +
                    sat[idxSAT(xa, ya)]
        }

        for (y in 0 until h) {
            val y0 = y - half
            val y1 = y + half + 1
            for (x in 0 until w) {
                val x0 = x - half
                val x1 = x + half + 1
                val area = (x1 - x0).coerceIn(1, w) * (y1 - y0).coerceIn(1, h)
                val localMean = (sumRect(x0, y0, x1, y1) / area).toInt()
                val gVal = gray[y * w + x]
                val v = if (gVal >= (localMean - offset)) 255 else 0
                out[y * w + x] = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
            }
        }

        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            setPixels(out, 0, w, 0, 0, w, h)
        }
    }
}
