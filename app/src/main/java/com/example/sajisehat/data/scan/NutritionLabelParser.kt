// com/example/sajisehat/data/scan/NutritionLabelParser.kt
package com.example.sajisehat.data.scan

import com.example.sajisehat.data.scan.model.NutritionScanResult
import kotlin.math.roundToInt

/**
 * Parser sederhana untuk teks label gizi berbahasa Indonesia.
 *
 * Tujuan awal:
 * - Menemukan "Takaran saji", "Jumlah sajian per kemasan",
 *   dan "Gula" / "Total gula" berdasarkan pola umum.
 *
 * Nanti bisa kamu perkuat dengan regex yang lebih kompleks
 * atau rule-based NLP sesuai variasi label di lapangan.
 */
class NutritionLabelParser {

    fun parse(rawText: String): NutritionScanResult {
        val normalized = rawText.lowercase()

        val servingSize = extractServingSizeGram(normalized)
        val servingsPerPack = extractServingsPerPack(normalized)
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

    private fun extractServingSizeGram(text: String): Double? {
        // Contoh pola:
        // "takaran saji 85 g" atau "takaran saji : 85 gram"
        val regex = Regex("""takaran\s+saji[^0-9]*([0-9]+)\s*(g|gram)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toDoubleOrNull()
    }

    private fun extractServingsPerPack(text: String): Int? {
        // Contoh pola:
        // "jumlah sajian per kemasan 3" atau "jumlah sajian/kemasan : 3"
        val regex = Regex("""jumlah\s+sajian[^0-9]*([0-9]+)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toIntOrNull()
    }

    private fun extractSugarPerServingGram(text: String): Double? {
        // Coba cari pola gula per sajian:
        // "gula total 8 g", "gula 8 gram", "gula per sajian 8 g"
        val regex = Regex("""gula[^\n0-9]*([0-9]+(\.[0-9]+)?)\s*(g|gram)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toDoubleOrNull()
    }

    private fun extractSugarPerPackGram(text: String): Double? {
        // Opsional: jika label menuliskan gula per kemasan
        // contoh: "gula per kemasan 24 g"
        val regex = Regex("""gula[^\n]*per\s+kemasan[^\n0-9]*([0-9]+(\.[0-9]+)?)\s*(g|gram)""")
        val match = regex.find(text) ?: return null
        return match.groupValues.getOrNull(1)?.toDoubleOrNull()
    }

    private fun extractProductName(rawText: String): String? {
        // Placeholder: sementara tidak benar-benar ekstrak, bisa diperkaya nanti.
        // Misal: ambil 1–2 baris pertama teks sebelum kata "informasi nilai gizi".
        val marker = "informasi nilai gizi"
        val index = rawText.lowercase().indexOf(marker)
        if (index <= 0) return null

        val header = rawText.substring(0, index)
        val lines = header.lines().map { it.trim() }.filter { it.isNotEmpty() }
        return lines.lastOrNull()
    }

    /**
     * Contoh helper untuk hitung % kebutuhan gula harian
     * kalau nanti kamu butuh di lapisan domain/VM.
     */
    fun calculateDailySugarPercentage(
        sugarPerServingGram: Double?,
        dailyLimitGram: Double = 50.0   // contoh default 50 g/hari
    ): Int? {
        if (sugarPerServingGram == null || dailyLimitGram <= 0.0) return null
        val percent = (sugarPerServingGram / dailyLimitGram) * 100.0
        return percent.roundToInt()
    }
}
