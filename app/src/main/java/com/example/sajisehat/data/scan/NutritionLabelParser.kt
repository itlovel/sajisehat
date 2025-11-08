package com.example.sajisehat.data.scan

import com.example.sajisehat.data.scan.model.NutritionScanResult
import kotlin.math.roundToInt

class NutritionLabelParser {

    fun parse(rawText: String): NutritionScanResult {
        val normalized = normalizeText(rawText)

        val servingSize = extractServingSizeGram(normalized)
        val servingsPerPack = extractServingsPerPack(normalized)

        // 🎯 ekstraksi gula yang lebih agresif
        val sugarPerServing = extractSugarPerServingGram(normalized)
        val sugarPerPack = extractSugarPerPackGram(normalized)

        val finalSugarPerServing = when {
            sugarPerServing != null -> sugarPerServing
            sugarPerPack != null && servingsPerPack != null && servingsPerPack > 0 ->
                sugarPerPack / servingsPerPack.toDouble()
            else -> null
        }

        val finalSugarPerPack = when {
            sugarPerPack != null -> sugarPerPack
            finalSugarPerServing != null && servingsPerPack != null ->
                finalSugarPerServing * servingsPerPack
            else -> null
        }

        val productName = extractProductName(rawText)

        return NutritionScanResult(
            rawText = rawText,
            productName = productName,
            servingSizeGram = servingSize,
            servingsPerPack = servingsPerPack,
            sugarPerServingGram = finalSugarPerServing,
            sugarPerPackGram = finalSugarPerPack
        )
    }

    // ========= NORMALISASI TEKS =========

    private fun normalizeText(text: String): String {
        return text
            .replace("\u00A0", " ")          // non-breaking space
            .replace(Regex("\\s+"), " ")     // spasi berulang
            .lowercase()
    }

    // ========= EKSTRAK TAKARAN SAJI & SAJIAN =========

    private fun extractServingSizeGram(text: String): Double? {
        // contoh: "takaran saji 30 g", "takaran saji : 30 gram"
        val regex = Regex("""takaran\s+saji[^0-9]*([0-9]+)\s*(g|gram)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toDoubleOrNull()
    }

    private fun extractServingsPerPack(text: String): Int? {
        // Pola umum: "jumlah sajian per kemasan 3"
        val r1 = Regex("""jumlah\s+sajian[^0-9]*([0-9]+)""")
        r1.find(text)?.let {
            return it.groupValues.getOrNull(1)?.toIntOrNull()
        }

        // Pola lain: "3 sajian per kemasan"
        val r2 = Regex("""([0-9]+)\s+sajian\s+per\s+kemasan""")
        r2.find(text)?.let {
            return it.groupValues.getOrNull(1)?.toIntOrNull()
        }

        return null
    }


    // ========= EKSTRAK GULA (lebih agresif) =========

    /**
     * Cari gula per sajian:
     * 1. cari baris yang mengandung "gula" / "sugar"
     * 2. normalisasi (perbaiki spasi, koma, 3q → 3 g, dll)
     * 3. coba beberapa pola regex
     */
    private fun extractSugarPerServingGram(text: String): Double? {
        val lines = text.split("\n", "\r")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var best: Double? = null

        fun updateBest(v: Double?) {
            if (v == null) return
            if (v < 0.1 || v > 100.0) return   // batas wajar
            best = v
        }

        for (rawLine in lines) {
            // Hanya baris yang benar-benar menyebut gula
            val hasSugarWord = rawLine.contains("gula", ignoreCase = true) ||
                    rawLine.contains("sugar", ignoreCase = true)
            if (!hasSugarWord) continue

            val line = normalizeSugarLine(rawLine)

            // Kalau baris ini juga mengandung kata "takaran" atau "saji", skip
            // untuk menghindari kasus "takaran saji 20 g gula ..." yang aneh
            if (line.contains("takaran saji") || line.contains("takaran", true)) {
                // tapi kalau ada pola "gula 1 g" yang jelas, tetap boleh
                val safe = Regex(
                    """gula( total| tambahan)?[:=\s]+([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\b"""
                ).find(line)
                if (safe != null) {
                    updateBest(safe.groupValues[2].toDoubleOrNull())
                    continue
                }
                continue
            }

            // Pola 1: "gula 1 g", "gula total: 8 g", "total sugar 10 g"
            val m1 = Regex(
                """(gula( total| tambahan)?|total\s*sugar|sugars?|added\s*sugars?)[:=\s]+([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\b"""
            ).find(line)
            if (m1 != null) {
                updateBest(m1.groupValues[3].toDoubleOrNull())
                continue
            }

            // Pola 2: "1 g gula", "8 g gula total"
            val m2 = Regex(
                """([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\s*(gula( total| tambahan)?|total\s*sugar|sugars?|added\s*sugars?)"""
            ).find(line)
            if (m2 != null) {
                updateBest(m2.groupValues[1].toDoubleOrNull())
                continue
            }

            // Pola 3: baris seperti "gula 1 g 3%" → ambil angka + g TERDEKAT setelah kata gula
            val idxSugar = line.indexOf("gula")
            if (idxSugar >= 0) {
                val after = line.substring(idxSugar)
                val m3 = Regex("""([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\b""").find(after)
                if (m3 != null) {
                    updateBest(m3.groupValues[1].toDoubleOrNull())
                    continue
                }
            }
        }

        return best
    }

    /**
     * Kalau di label ada info gula per kemasan,
     * misal "gula per kemasan 24 g" / "total sugar per pack 24 g"
     */
    private fun extractSugarPerPackGram(text: String): Double? {
        val lines = text.split("\n", "\r")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (rawLine in lines) {
            if (!rawLine.contains("gula") && !rawLine.contains("sugar")) continue
            val line = normalizeSugarLine(rawLine)

            val m = Regex(
                """(per\s+kemasan|per\s+pack|per\s+pak)\s*[:=\s]*([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\b"""
            ).find(line)
            if (m != null) {
                return m.groupValues[2].toDoubleOrNull()
            }
        }
        return null
    }

    /**
     * Normalisasi khusus baris gula:
     * - "1 9 g" → "19 g"
     * - "3 q g" → "3 g"
     * - "1 , 0" → "1.0"
     * - "1O g" → "10 g"
     */
    private fun normalizeSugarLine(line0: String): String {
        var s = line0.lowercase()

        s = s.replace(Regex("\\s+"), " ")

        // "1 9 g" / "1 0 g" dll -> gabung
        s = s.replace(Regex("(\\d)\\s+(\\d)(?=\\s*(g|gr|gram|%))"), "$1$2")

        // "3 q" / "3 9" -> "3 g"
        s = s.replace(Regex("(\\d)\\s+(9|q)(?=\\s*(g|gr|gram|%))"), "$1g")

        // "1 , 0" => "1.0"
        s = s.replace(Regex("(\\d)\\s*,\\s*(\\d)"), "$1.$2")

        // huruf O sebelum satuan → 0
        s = s.replace(
            Regex("(?<=\\d)o(?=\\s*(g|gr|gram|%))", RegexOption.IGNORE_CASE),
            "0"
        )

        // koma → titik
        s = s.replace(",", ".")

        return s
    }

    // ========= PRODUCT NAME =========
    private fun extractProductName(rawText: String): String? {
        val lower = rawText.lowercase()
        val markerIndex = lower.indexOf("informasi nilai gizi")

        // Kalau ketemu section "INFORMASI NILAI GIZI", ambil teks di atasnya
        val header = if (markerIndex > 0) {
            rawText.substring(0, markerIndex)
        } else {
            rawText
        }

        val lines = header.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filterNot { it.startsWith("=====", ignoreCase = true) } // buang "===== PAGE 1 ====="

        return lines.lastOrNull()
    }


    // ========= UTIL OPSIONAL: % kebutuhan harian =========

    fun calculateDailySugarPercentage(
        sugarPerServingGram: Double?,
        dailyLimitGram: Double = 50.0
    ): Int? {
        if (sugarPerServingGram == null || dailyLimitGram <= 0.0) return null
        val percent = (sugarPerServingGram / dailyLimitGram) * 100.0
        return percent.roundToInt()
    }
}
