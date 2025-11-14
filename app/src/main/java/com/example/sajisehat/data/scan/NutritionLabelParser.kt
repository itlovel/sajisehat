package com.example.sajisehat.data.scan

import com.example.sajisehat.data.scan.model.NutritionScanResult
import kotlin.math.roundToInt

class NutritionLabelParser {

    fun parse(rawText: String): NutritionScanResult {
        // 1) Versi per baris (hasil OCR apa adanya, cuma dibersihkan sedikit)
        val originalLines = rawText
            .lines()
            .map { it.replace("\u00A0", " ").trim() }
            .filter { it.isNotEmpty() }

        // === NEW: gabungkan baris yang termasuk satu "row gizi" ===
        val lines = mergeNutritionRows(originalLines)

        // 2) Versi "flat" untuk regex yang tidak peduli baris
        val normalizedFlat = normalizeText(rawText)

        // ========= TAKARAN SAJI & JUMLAH SAJIAN =========
        val servingSize = extractServingSizeGramFromLines(lines)
            ?: extractServingSizeGram(normalizedFlat)    // fallback

        val servingsPerPack = extractServingsPerPackFromLines(lines)
            ?: extractServingsPerPack(normalizedFlat)    // fallback

        // ========= GULA (PER SAJIAN & PER KEMASAN) =========
        val sugarPerServing = extractSugarPerServingGramFromLines(lines)
        val sugarPerPack = extractSugarPerPackGramFromLines(lines)

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

    // ========= NORMALISASI TEKS GLOBAL =========

    private fun normalizeText(text: String): String {
        return text
            .replace("\u00A0", " ")          // non-breaking space → spasi normal
            .replace(Regex("[ \t]+"), " ")   // gabungkan SPASI & TAB, newline tetap
            .lowercase()
    }

    // ========= HELPER UNTUK ROW NUTRISI (NEW) =========

    private val nutrientKeywords = listOf(
        "lemak", "protein", "karbohidrat", "karbohidrat total",
        "gula", "sugar", "natrium", "garam", "sodium", "energi", "energy"
    )

    private fun hasNumber(s: String): Boolean =
        s.any { it.isDigit() }

    private fun containsSugarWord(s: String): Boolean =
        s.contains("gula", ignoreCase = true) || s.contains("sugar", ignoreCase = true)

    /**
     * Menggabungkan baris nutrisi yang terbelah karena layout tabel.
     *
     * Contoh OCR:
     *   "Gula Total"
     *   "4 g"
     * Menjadi:
     *   "Gula Total 4 g"
     *
     * Aturan:
     * - Jika satu baris punya kata kunci nutrisi tapi tidak ada angka,
     *   dan baris di bawahnya ada angka + satuan (g/mg/%),
     *   dua baris itu digabung.
     */
    private fun mergeNutritionRows(lines: List<String>): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < lines.size) {
            val cur = lines[i].trim()
            val curLower = cur.lowercase()

            val isNutrientLine = nutrientKeywords.any { kw ->
                curLower.contains(kw)
            }

            val curHasNumber = hasNumber(cur)

            if (isNutrientLine && !curHasNumber && i + 1 < lines.size) {
                val next = lines[i + 1].trim()
                val nextLower = next.lowercase()

                val nextHasNumber = hasNumber(nextLower)
                val nextHasUnit = Regex("\\b(g|gr|gram|mg|mcg|µg|%)\\b").containsMatchIn(nextLower)

                if (nextHasNumber && nextHasUnit) {
                    // gabung dua baris sebagai satu row
                    result.add("$cur $next")
                    i += 2
                    continue
                }
            }

            result.add(cur)
            i++
        }
        return result
    }

    // ========= TAKARAN SAJI & SAJIAN (FALLBACK "FLAT") =========

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

    // ========= TAKARAN SAJI & SAJIAN (LINE-BASED / ROW) =========

    private fun extractServingSizeGramFromLines(lines: List<String>): Double? {
        for (rawLine in lines) {
            val line = normalizeText(rawLine)
            val regex = Regex("""takaran\s+saji[^0-9]*([0-9]+)\s*(g|gram)""")
            val match = regex.find(line)
            if (match != null) {
                return match.groupValues.getOrNull(1)?.toDoubleOrNull()
            }
        }
        return null
    }

    private fun extractServingsPerPackFromLines(lines: List<String>): Int? {
        var foundKeywordLine = false

        for (rawLine in lines) {
            val line = normalizeText(rawLine)

            if (line.contains("jumlah sajian per kemasan")) {
                foundKeywordLine = true
            }

            // Pola umum: "jumlah sajian per kemasan 3"
            val r1 = Regex("""jumlah\s+sajian[^0-9]*([0-9]+)""")
            val m1 = r1.find(line)
            if (m1 != null) {
                return m1.groupValues.getOrNull(1)?.toIntOrNull()
            }

            // Pola lain: "3 sajian per kemasan"
            val r2 = Regex("""([0-9]+)\s+sajian\s+per\s+kemasan""")
            val m2 = r2.find(line)
            if (m2 != null) {
                return m2.groupValues.getOrNull(1)?.toIntOrNull()
            }
        }

        // NEW: kalau ada teks "jumlah sajian per kemasan" tapi tidak ada angka,
        // asumsikan 1 sajian per kemasan (seperti label yang kamu kirim).
        if (foundKeywordLine) return 1

        return null
    }

    // ========= GULA PER SAJIAN (LINE-BASED) =========

    private fun extractSugarPerServingGramFromLines(lines: List<String>): Double? {
        var best: Double? = null

        fun updateBest(v: Double?) {
            if (v == null) return
            if (v < 0.1 || v > 100.0) return   // batas wajar nilai gula per sajian
            best = v
        }

        for (rawLine in lines) {
            val hasSugarWord = containsSugarWord(rawLine)
            if (!hasSugarWord) continue

            val line = normalizeSugarLine(rawLine)

            // Jika baris ini ada "takaran saji" biasanya bukan baris gula utama
            if (line.contains("takaran saji")) {
                // tapi kalau ada pola "gula 1 g" yang jelas, tetap boleh
                val safe = Regex(
                    """gula( total| tambahan)?[:=\s]+([0-9]+(?:\.[0-9]+)?)\s*(g|gr|gram)\b"""
                ).find(line)
                if (safe != null) {
                    updateBest(safe.groupValues[2].toDoubleOrNull())
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

            // Pola 3: "gula 1 g 3%" → ambil angka + g terdekat setelah kata "gula"
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

    // ========= GULA PER KEMASAN (LINE-BASED) =========

    private fun extractSugarPerPackGramFromLines(lines: List<String>): Double? {
        for (rawLine in lines) {
            val hasSugarWord = containsSugarWord(rawLine)
            if (!hasSugarWord) continue

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

    // ========= NORMALISASI KHUSUS BARIS GULA =========

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

    // ========= NAMA PRODUK =========

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
