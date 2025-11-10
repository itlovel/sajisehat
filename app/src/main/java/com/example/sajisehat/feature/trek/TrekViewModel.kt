package com.example.sajisehat.feature.trek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.data.trek.model.DailySugarData
import com.example.sajisehat.feature.trek.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class TrekViewModel(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrekUiState())
    val uiState: StateFlow<TrekUiState> = _uiState

    private val today: LocalDate = LocalDate.now()
    private var currentMonth: YearMonth = YearMonth.from(today)

    init {
        refreshAll()
    }

    fun onPrevMonth() {
        currentMonth = currentMonth.minusMonths(1)
        refreshMonth()
    }

    fun onNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        refreshMonth()
    }

    private fun refreshAll() {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: run {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User belum login") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Hari ini
                val todayTotal = trekRepository.getTotalSugarForDate(user.uid, today)
                val todaySummary = buildTodaySummary(todayTotal)

                // Minggu ini
                val weekStart = today.with(DayOfWeek.MONDAY)
                val weekEnd = weekStart.plusDays(6)
                val weekData = trekRepository.getTotalSugarForDateRange(user.uid, weekStart, weekEnd)
                val weekSummary = buildWeekSummary(weekStart, weekData)

                // Bulan ini
                val monthStart = currentMonth.atDay(1)
                val monthEnd = currentMonth.atEndOfMonth()
                val monthData = trekRepository.getTotalSugarForDateRange(user.uid, monthStart, monthEnd)
                val monthSummary = buildMonthSummary(currentMonth, monthData)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todaySummary = todaySummary,
                        weekSummary = weekSummary,
                        monthSummary = monthSummary
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Terjadi kesalahan saat memuat data trek"
                    )
                }
            }
        }
    }

    private fun refreshMonth() {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch

            try {
                val monthStart = currentMonth.atDay(1)
                val monthEnd = currentMonth.atEndOfMonth()
                val monthData = trekRepository.getTotalSugarForDateRange(user.uid, monthStart, monthEnd)
                val monthSummary = buildMonthSummary(currentMonth, monthData)

                _uiState.update {
                    it.copy(monthSummary = monthSummary)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Gagal memuat data bulanan")
                }
            }
        }
    }

    // ================== HELPER BUILDER ==================

    private fun buildTodaySummary(totalGram: Double): TodaySummaryUi {
        val percent = (totalGram / 50.0 * 100.0)
            .coerceIn(0.0, 100.0)
            .toInt()

        val level = when {
            totalGram <= 0.0 -> SugarLevelUi.UNKNOWN
            totalGram <= 5.0 -> SugarLevelUi.LOW
            totalGram <= 15.0 -> SugarLevelUi.MEDIUM
            else -> SugarLevelUi.HIGH
        }

        return TodaySummaryUi(
            level = level,
            totalGram = totalGram,
            percentageOfNeed = percent
        )
    }

    private fun buildWeekSummary(
        weekStart: LocalDate,
        weekData: List<DailySugarData>
    ): WeekSummaryUi {
        val map = weekData.associateBy { it.date }

        val days = (0..6).map { offset ->
            val date = weekStart.plusDays(offset.toLong())
            val data = map[date]
            WeekDayUi(
                date = date,
                totalGram = data?.totalGram ?: 0.0
            )
        }

        val weekOfMonth = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfMonth())
        val monthName = weekStart.month.getDisplayName(TextStyle.FULL, Locale("id"))
        val label = "Minggu ke-$weekOfMonth $monthName"

        val note = "Kunci dari kesuksesan adalah konsistensi"

        return WeekSummaryUi(
            label = label,
            note = note,
            days = days
        )
    }

    private fun buildMonthSummary(
        yearMonth: YearMonth,
        monthData: List<DailySugarData>
    ): MonthSummaryUi {
        val map = monthData.associateBy { it.date }

        val days = (1..yearMonth.lengthOfMonth()).map { day ->
            val date = yearMonth.atDay(day)
            val data = map[date]
            MonthDayUi(
                date = date,
                totalGram = data?.totalGram ?: 0.0
            )
        }

        return MonthSummaryUi(
            yearMonth = yearMonth,
            days = days
        )
    }
}
