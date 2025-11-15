package com.example.sajisehat.data.scan

import com.example.sajisehat.data.scan.model.NutritionRawSegments
import com.example.sajisehat.data.scan.model.NutritionScanResult
import kotlin.math.roundToInt

class NutritionLabelParser {

    // =======================
    //  HELPER NUMERIC
    // =======================

    private fun toFloat(numStr: String?): Double? {
        if (numStr.isNullOrBlank()) return null
        val fixed = numStr.replace(",", ".")
        return fixed.toDoubleOrNull()
    }

    private fun allNumbers(line: String): List<Double> {
        val regex = Regex("""(\d+(?:[.,]\d+)?)""")
        return regex.findAll(line)
            .mapNotNull { toFloat(it.groupValues[1]) }
            .toList()
    }

    // =======================
    //  CLEANING TEKS (PORT _clean_text)
    // =======================

    private fun cleanText(rawText: String?): String {
        var text = (rawText ?: "").lowercase()

        // 1. ubah pola "7 9", "11 9" menjadi "7 g", "11 g"
        text = Regex("""(\d+)\s+9\b""").replace(text, "$1 g")

        // 2. khusus takaran saji: "takaran saji 209" → "takaran saji 20 g"
        val patternTakaran = Regex("""takaran saji\s*(\d{2,3})9\b""")
        text = patternTakaran.replace(text) { m ->
            val numberWith9 = m.groupValues[1] // misal "209"
            val fixedNumber =
                if (numberWith9.length >= 2) numberWith9.dropLast(1) else numberWith9
            "takaran saji $fixedNumber g"
        }

        // 3. variasi tulisan sajian per kemasan
        text = Regex("""sajian\s*/\s*kemasan""").replace(text, "sajian per kemasan")

        return text
    }

    // =======================
    //  PARSER PER KOMPONEN (PORT DARI PYTHON)
    // =======================

    // ---- Takaran saji (gram) ----
    // ---- Takaran saji (gram) ----
    private fun parseServingSizeGram(lines: List<String>): Double? {
        if (lines.isEmpty()) return null

        // 0) LANGKAH CEPAT: baris yang eksplisit menyebut "takaran saji" / "serving size"
        for (raw in lines) {
            val line = raw.lowercase()

            if (line.contains("takaran saji") || line.contains("serving size")) {
                // contoh: "takaran saji/serving size: 25 g (5 stik/5 sticks)"
                val direct = Regex("""(\d+(?:[.,]\d+)?)\s*(g|gram|ml)\b""")
                    .find(line)
                if (direct != null) {
                    val v = toFloat(direct.groupValues[1])
                    if (v != null) return v
                }
            }
        }

        // 1) LOGIC WINDOW: dari "takaran saji" sampai "sajian per kemasan"
        val full = lines.joinToString("\n").lowercase()

        val idx = full.indexOf("takaran saji")
        if (idx != -1) {
            var endIdx = full.indexOf("sajian per kemasan", idx)
            if (endIdx == -1) {
                endIdx = (idx + 200).coerceAtMost(full.length)  // batas aman
            }

            val window = full.substring(idx, endIdx)

            // 1a. Cari angka utuh + unit di window
            val regex1 = Regex("""(\d+(?:[.,]\d+)?)\s*(g|gram|ml)\b""")
            val m = regex1.find(window)
            if (m != null) {
                val v = toFloat(m.groupValues[1])
                if (v != null) return v
            }

            // 1b. Kalau belum dapat, ambil semua angka di window
            val nums = allNumbers(window)
            if (nums.isNotEmpty()) {
                // kasus umum: "1 4 g" → [1, 4] → 14
                if (nums.size >= 2 && nums[0] < 10 && nums[1] < 10) {
                    return (10 * nums[0].toInt() + nums[1].toInt()).toDouble()
                }

                // kasus seperti "1 1 4 g" → [1,1,4]
                if (nums.size >= 3 && nums[0] == 1.0 && nums[1] == 1.0 && nums[2] < 10) {
                    return (10 * nums[1].toInt() + nums[2].toInt()).toDouble()
                }

                // kalau angka pertama sudah >= 10, pakai langsung
                if (nums[0] >= 10) return nums[0]
            }
        }

        // 2) Fallback: cek per baris yang mengandung "takaran saji"
        for (i in lines.indices) {
            val l = lines[i].lowercase()
            if (!l.contains("takaran saji")) continue

            var combo = l
            if (i + 1 < lines.size) {
                combo += " " + lines[i + 1].lowercase()
            }

            val regex2 = Regex("""(\d+(?:[.,]\d+)?)\s*(g|gram|ml)\b""")
            val m2 = regex2.find(combo)
            if (m2 != null) {
                val v = toFloat(m2.groupValues[1])
                if (v != null) return v
            }

            val nums = allNumbers(combo)
            if (nums.isNotEmpty()) {
                if (nums.size >= 2 && nums[0] < 10 && nums[1] < 10) {
                    return (10 * nums[0].toInt() + nums[1].toInt()).toDouble()
                }
                if (nums[0] >= 10) return nums[0]
                return nums[0]
            }
        }

        return null
    }

    // ---- Sajian per kemasan ----
    // ---- Jumlah sajian per kemasan ----
    private fun parseServingsPerPack(lines: List<String>): Int? {
        if (lines.isEmpty()) return null

        // Normalisasi semua baris (perbaiki typo sajian/kemasan dan slash)
        val normalizedLines = lines.map { normalizeServingsLine(it) }

        // ---------- 1) Pola global: "6 sajian per kemasan" / "6 servings per container" ----------
        val joined = normalizedLines.joinToString(" ")

        val pattern = Regex(
            // dukung: "6 sajian per kemasan", "6 servings per container", "6 servings per pack"
            """(\d+(?:[.,]\d+)?)\s*(""" +
                    """sajian\s*(?:per|/)?\s*kemasan|""" +
                    """porsi\s*(?:per|/)?\s*kemasan|""" +
                    """servings?\s+per\s+(pack|pak|container)""" +
                    """)""",
            RegexOption.IGNORE_CASE
        )

        val m = pattern.find(joined)
        if (m != null) {
            val v = toFloat(m.groupValues[1])
            if (v != null) return v.roundToInt()
        }

        // ---------- 2) Fallback per baris: cek baris ini + atas + bawah ----------
        var foundKeywordLine = false

        for (i in normalizedLines.indices) {
            val line = normalizedLines[i]

            val hasIdn = line.contains("sajian") && line.contains("kemasan")
            val hasEn = line.contains("serving") && (line.contains("pack") || line.contains("container"))
            if (!hasIdn && !hasEn) continue

            foundKeywordLine = true

            // coba ambil angka di BARIS INI
            var nums = allNumbers(line)

            // kalau nggak ada, cek BARIS BERIKUTNYA (angka di bawah teks)
            if (nums.isEmpty() && i + 1 < normalizedLines.size) {
                val comboNext = line + " " + normalizedLines[i + 1]
                nums = allNumbers(comboNext)
            }

            // kalau masih nggak ada, cek BARIS SEBELUMNYA (angka di atas teks)
            if (nums.isEmpty() && i - 1 >= 0) {
                val comboPrev = normalizedLines[i - 1] + " " + line
                nums = allNumbers(comboPrev)
            }

            if (nums.isNotEmpty()) {
                return nums[0].roundToInt()
            }
        }

        // ---------- 3) Kalau ada teks "sajian per kemasan" tapi benar-benar tanpa angka ----------
        // fallback aman: anggap 1 sajian per kemasan
        if (foundKeywordLine) return 1

        return null
    }


    // ---- Gula per sajian ----
    private fun parseSugarPerServing(lines: List<String>): Double? {
        if (lines.isEmpty()) return null

        // 🔧 NORMALISASI dulu semua kemungkinan "gula" yang salah baca
        val joined = normalizeGulaLikeWords(
            lines.joinToString("\n").lowercase()
        )

        // 1) Pencarian global pakai window di sekitar 'gula'
        val patterns = listOf(
            Regex(
                """gula\s+total[^\d]{0,80}(\d+(?:[.,]\d+)?)\s*(g|gram|mg)?""",
                setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
            ),
            Regex(
                """\bgula\b[^\d]{0,80}(\d+(?:[.,]\d+)?)\s*(g|gram|mg)?""",
                setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
            )
        )

        for (pattern in patterns) {
            val m = pattern.find(joined)
            if (m != null) {
                val v = toFloat(m.groupValues[1])
                val unit = m.groupValues.getOrNull(2)?.lowercase() ?: ""
                if (v != null) {
                    return if (unit == "mg") v / 1000.0 else v
                }
            }
        }

        // 2) Fallback per-baris
        val smallNumUnitRegex =
            Regex("""(\d+(?:[.,]\d+)?)\s*(g|gram|mg)?""", RegexOption.IGNORE_CASE)

        for (i in lines.indices) {
            // 🔧 NORMALISASI "gula" per baris juga
            val lineL = normalizeGulaLikeWords(lines[i].lowercase())
            if (!lineL.contains("gula") && !lineL.contains("sugar")) continue

            var combo = lineL
            if (i + 1 < lines.size) {
                combo += " " + normalizeGulaLikeWords(lines[i + 1].lowercase())
            }

            val matches = smallNumUnitRegex.findAll(combo).toList()
            if (matches.isEmpty()) continue

            val first = matches[0]
            val numStr = first.groupValues[1]
            val unitStr = first.groupValues.getOrNull(2) ?: ""
            val v = toFloat(numStr) ?: continue

            val unit = unitStr.lowercase()
            return if (unit == "mg") v / 1000.0 else v
        }

        return null
    }


    // ---- Gula per kemasan ----
    private fun parseSugarPerPack(lines: List<String>): Double? {
        val pattern = Regex(
            """(gula total per kemasan|total sugar per pack)[^0-9]{0,20}(\d+(?:[.,]\d+)?)\s*(g|gram)?""",
            RegexOption.IGNORE_CASE
        )

        for (line in lines) {
            val m = pattern.find(line)
            if (m != null) {
                return toFloat(m.groupValues[2])
            }
        }

        return null
    }

    // =======================
    //  MAIN PARSER (PORT parse_nutrition)
    // =======================

    // Overload: pakai segmen (Roboflow ROI)
    fun parse(segments: NutritionRawSegments): NutritionScanResult {
        val takaranText = segments.takaranSaji
        val sajianText = segments.sajianPerKemasan
        val gulaText = segments.gula

        // rawText sintetis dari gabungan segmen
        val syntheticRawText = buildString {
            takaranText?.let { appendLine(it) }
            sajianText?.let { appendLine(it) }
            gulaText?.let { appendLine(it) }
        }.trim()

        return internalParse(
            rawText = syntheticRawText,
            textTakaran = takaranText,
            textSajian = sajianText,
            textGula = gulaText
        )
    }

    // Overload: cuma punya teks global
    fun parse(rawText: String): NutritionScanResult {
        return internalParse(
            rawText = rawText,
            textTakaran = null,
            textSajian = null,
            textGula = null
        )
    }

    private fun internalParse(
        rawText: String?,
        textTakaran: String?,
        textSajian: String?,
        textGula: String?
    ): NutritionScanResult {

        // --- CLEAN & split global text (union) ---
        val cleanedGlobal = cleanText(rawText ?: "")
        val linesGlobal =
            if (cleanedGlobal.isNotEmpty()) cleanedGlobal.lines() else emptyList()

        // --- CLEAN & split segment teks (kalau ada) ---
        val cleanedTakaran = if (!textTakaran.isNullOrBlank()) cleanText(textTakaran) else ""
        val linesTakaran =
            if (cleanedTakaran.isNotEmpty()) cleanedTakaran.lines() else emptyList()

        val cleanedSajian = if (!textSajian.isNullOrBlank()) cleanText(textSajian) else ""
        val linesSajian =
            if (cleanedSajian.isNotEmpty()) cleanedSajian.lines() else emptyList()

        val cleanedGula = if (!textGula.isNullOrBlank()) cleanText(textGula) else ""
        val linesGula =
            if (cleanedGula.isNotEmpty()) cleanedGula.lines() else emptyList()

        // -----------------------------
        // 1. Takaran saji (gram)
        // -----------------------------
        var servingSizeGram: Double? = null

        // a) Prioritas dari ROI takaran_saji
        if (linesTakaran.isNotEmpty()) {
            servingSizeGram = parseServingSizeGram(linesTakaran)
        }

        // b) Fallback dari teks global
        if (servingSizeGram == null && linesGlobal.isNotEmpty()) {
            servingSizeGram = parseServingSizeGram(linesGlobal)
        }

        // -----------------------------
        // 2. Sajian per kemasan
        // -----------------------------
        var servingsPerPack: Int? = null

        // a) Prioritas dari ROI sajian_per_kemasan
        if (linesSajian.isNotEmpty()) {
            servingsPerPack = parseServingsPerPack(linesSajian)
        }

        // b) Fallback dari teks global
        if (servingsPerPack == null && linesGlobal.isNotEmpty()) {
            servingsPerPack = parseServingsPerPack(linesGlobal)
        }

        // -----------------------------
        // 3. Gula per sajian & per kemasan
        // -----------------------------
        var sugarPerServingGram: Double? = null
        var sugarPerPackGram: Double? = null

        // a) Prioritas dari ROI 'gula'
        if (linesGula.isNotEmpty()) {
            sugarPerServingGram = parseSugarPerServing(linesGula)
            sugarPerPackGram = parseSugarPerPack(linesGula)

            // Fallback ekstra: kalau masih null, pakai angka paling masuk akal dari text_gula mentah
            if (sugarPerServingGram == null && !textGula.isNullOrBlank()) {
                val cleanedGulaRaw = cleanText(textGula)
                val nums = allNumbers(cleanedGulaRaw)
                val candidates = nums.filter { it > 0.0 && it < 60.0 }
                if (candidates.isNotEmpty()) {
                    sugarPerServingGram = candidates.maxOrNull()
                }
            }
        }

        // b) Fallback dari teks global (union)
        if (sugarPerServingGram == null && linesGlobal.isNotEmpty()) {
            sugarPerServingGram = parseSugarPerServing(linesGlobal)
        }
        if (sugarPerPackGram == null && linesGlobal.isNotEmpty()) {
            sugarPerPackGram = parseSugarPerPack(linesGlobal)
        }

        // c) Turunan: kalau gula per kemasan None, tapi punya gula per sajian + jumlah sajian
        if (sugarPerPackGram == null &&
            sugarPerServingGram != null &&
            servingsPerPack != null
        ) {
            sugarPerPackGram = sugarPerServingGram * servingsPerPack
        }

        // product_name biarkan null (user isi manual di app)
        val productName: String? = null

        return NutritionScanResult(
            rawText = rawText ?: "",
            productName = productName,
            servingSizeGram = servingSizeGram,
            servingsPerPack = servingsPerPack,
            sugarPerServingGram = sugarPerServingGram,
            sugarPerPackGram = sugarPerPackGram
        )
    }

    // =======================
    //  UTIL OPSIONAL: % kebutuhan harian
    // =======================

    fun calculateDailySugarPercentage(
        sugarPerServingGram: Double?,
        dailyLimitGram: Double = 50.0
    ): Int? {
        if (sugarPerServingGram == null || dailyLimitGram <= 0.0) return null
        val percent = (sugarPerServingGram / dailyLimitGram) * 100.0
        return percent.roundToInt()
    }

    // Normalisasi segala bentuk "gula" yang salah baca
    private fun normalizeGulaLikeWords(text0: String): String {
        var s = text0.lowercase()

        // "g u l a" -> "gula"
        s = Regex("g\\s*u\\s*l\\s*a").replace(s, "gula")

        // "9ula", "qula" -> "gula"
        s = Regex("[9q]ula").replace(s, "gula")

        // "gu1a", "guia" (1 / i ketuker dengan l) -> "gula"
        s = Regex("gu1a").replace(s, "gula")
        s = Regex("guia").replace(s, "gula")

        // "guta", "guka", "gola" → kita paksa jadi "gula" (OCR sering tukar huruf tengah)
        s = Regex("gu[tkc]a").replace(s, "gula")
        s = Regex("go?a").replace(s, "gula") // jaga-jaga "g0la" / "gola"

        // plus varian kecil seperti "g ula" → "gula"
        s = Regex("g\\s+ula").replace(s, "gula")

        return s
    }

    // Normalisasi segala bentuk "sajian per kemasan" yang salah baca
    private fun normalizeServingsLine(text0: String): String {
        var s = text0.lowercase()

        // variasi "sajian" salah baca
        s = Regex("saj[1i]an").replace(s, "sajian")
        s = Regex("sajlan|sajjan").replace(s, "sajian")

        // variasi "kemasan" salah baca
        s = Regex("kemas[anmn]").replace(s, "kemasan")
        s = Regex("kemsan|kemazan|kemasn").replace(s, "kemasan")

        // "sajian/kemasan" -> "sajian per kemasan"
        s = Regex("sajian\\s*/\\s*kemasan").replace(s, "sajian per kemasan")
        s = Regex("porsi\\s*/\\s*kemasan").replace(s, "porsi per kemasan")

        return s
    }





}
