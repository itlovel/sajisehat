package com.example.sajisehat.feature.trek.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.YearMonth

// Batas standar harian
const val DAILY_LIMIT_GRAM = 50.0

// Level gula untuk UI Trek (hari ini, detail, manual)
enum class SugarLevelUi {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

// Hitung level dari total gula harian (atau hasil manual)
fun getDailySugarLevel(totalSugarGram: Double?): SugarLevelUi =
    when {
        totalSugarGram == null -> SugarLevelUi.UNKNOWN
        totalSugarGram <= 25.0 -> SugarLevelUi.LOW
        totalSugarGram <= 50.0 -> SugarLevelUi.MEDIUM
        else -> SugarLevelUi.HIGH
    }

fun SugarLevelUi.titleText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Tinggi"
    SugarLevelUi.UNKNOWN -> "-"
}

fun SugarLevelUi.accentColor(): Color = when (this) {
    SugarLevelUi.LOW -> Color(0xFF0047BA)
    SugarLevelUi.MEDIUM -> Color(0xFFFFA726)
    SugarLevelUi.HIGH -> Color(0xFFE53935)
    SugarLevelUi.UNKNOWN -> Color(0xFFBDBDBD)
}

// Persen terhadap 50 gr per hari
fun calculateDailyPercent(totalSugarGram: Double?): Int {
    if (totalSugarGram == null) return 0
    val percent = (totalSugarGram / DAILY_LIMIT_GRAM) * 100.0
    // hanya dibatasi bawah = 0, atas dibiarkan (biar bisa 120%, 150%, dst)
    return percent.toInt().coerceAtLeast(0)
}

// === MODEL UNTUK TREK GULA ===

// Ringkasan konsumsi gula HARI INI
data class TodaySummaryUi(
    val level: SugarLevelUi,   // LOW / MEDIUM / HIGH / UNKNOWN
    val totalGram: Double,     // total gula hari ini
    val percentageOfNeed: Int  // persen dari 50 gr (0–100+)
)

// Data 1 hari dalam rekap MINGGU INI
data class WeekDayUi(
    val date: LocalDate,
    val totalGram: Double
)

// Ringkasan MINGGU INI
data class WeekSummaryUi(
    val label: String,
    val note: String,
    val days: List<WeekDayUi>
)

// Data 1 tanggal dalam kalender BULAN INI
data class MonthDayUi(
    val date: LocalDate,
    val totalGram: Double
)

// Ringkasan BULAN INI
data class MonthSummaryUi(
    val yearMonth: YearMonth,
    val days: List<MonthDayUi>
)

// State utama halaman Trek
data class TrekUiState(
    val isLoading: Boolean = true,
    val todaySummary: TodaySummaryUi? = null,
    val weekSummary: WeekSummaryUi? = null,
    val monthSummary: MonthSummaryUi? = null,
    val errorMessage: String? = null
)

// Item di halaman detail trek harian
data class TrekDetailItemUi(
    val id: String,
    val productName: String,
    val sugarGram: Double,
    val percentageOfDailyNeed: Int
)

// State halaman detail trek harian
data class TrekDetailUiState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val todaySummary: TodaySummaryUi? = null,
    val items: List<TrekDetailItemUi> = emptyList(),
    val errorMessage: String? = null
)

// State input manual
data class ManualInputUiState(
    val productName: String = "",
    val sugarPerServingText: String = "",
    val servingSizeText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isValid: Boolean = false
)

// Hasil kalkulasi manual (dipakai di layar ManualCalc & save)
data class ManualResultUi(
    val productName: String,
    val sugarPerServingGram: Double,
    val servingSizeGram: Double?,
    val percentOfDailyNeed: Int,
    val level: SugarLevelUi
)
