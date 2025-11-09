package com.example.sajisehat.feature.trek.model

import java.time.LocalDate
import java.time.YearMonth

// 1) Level gula untuk UI (dipakai card & warna)
enum class SugarLevelUi {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

// 2) Ringkasan konsumsi gula HARI INI
data class TodaySummaryUi(
    val level: SugarLevelUi,   // LOW / MEDIUM / HIGH / UNKNOWN
    val totalGram: Double,     // total gula hari ini
    val percentageOfNeed: Int  // persen dari 50 gr (0–100)
)

// 3) Data 1 hari dalam rekap MINGGU INI
data class WeekDayUi(
    val date: LocalDate,
    val totalGram: Double
)

// 4) Ringkasan MINGGU INI
data class WeekSummaryUi(
    val label: String,            // contoh: "Minggu ke-3 Mei"
    val note: String,             // contoh: "Kunci dari kesuksesan adalah konsistensi"
    val days: List<WeekDayUi>     // 7 item: Senin–Minggu
)

// 5) Data 1 tanggal dalam kalender BULAN INI
data class MonthDayUi(
    val date: LocalDate,
    val totalGram: Double
)

// 6) Ringkasan BULAN INI
data class MonthSummaryUi(
    val yearMonth: YearMonth,      // contoh: 2025-05
    val days: List<MonthDayUi>     // semua tanggal dalam bulan itu
)

// 7) State utama halaman Trek
data class TrekUiState(
    val isLoading: Boolean = true,
    val todaySummary: TodaySummaryUi? = null,
    val weekSummary: WeekSummaryUi? = null,
    val monthSummary: MonthSummaryUi? = null,
    val errorMessage: String? = null
)

data class TrekDetailItemUi(
    val id: String,
    val productName: String,
    val sugarGram: Double,
    val percentageOfDailyNeed: Int
)

data class TrekDetailUiState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val todaySummary: TodaySummaryUi? = null,
    val items: List<TrekDetailItemUi> = emptyList(),
    val errorMessage: String? = null
)

data class ManualInputUiState(
    val productName: String = "",
    val sugarPerServingText: String = "",
    val servingSizeText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isValid: Boolean = false
)

data class ManualResultUi(
    val productName: String,
    val sugarPerServingGram: Double,
    val servingSizeGram: Double?,
    val percentOfDailyNeed: Int,
    val level: SugarLevelUi
)
