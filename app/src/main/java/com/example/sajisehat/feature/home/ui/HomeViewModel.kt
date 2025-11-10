package com.example.sajisehat.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.home.FirebaseHomeRepository
import com.example.sajisehat.data.home.HomeRepository
import com.example.sajisehat.data.home.SugarSummary
import com.example.sajisehat.data.home.Tip
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.di.AppGraph
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class TipUi(val id: String, val imageUrl: String, val text: String)

data class HomeUiState(
    val userName: String = "Pengguna",
    val userPhotoUrl: String? = null,

    val tips: List<TipUi> = emptyList(),
    val loadingTips: Boolean = true,
    val errorTips: String? = null,

    val dayGrams: Int = 0,
    val weekGrams: Int = 0,
    val monthGrams: Int = 0,
    val loadingSugar: Boolean = true
)

class HomeViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val repo: HomeRepository = FirebaseHomeRepository(),
    private val trekRepository: TrekRepository = AppGraph.trekRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        val u = auth.currentUser
        _state.update {
            it.copy(
                userName = (u?.displayName ?: "").ifBlank { "Kamu" },
                userPhotoUrl = u?.photoUrl?.toString()
            )
        }

        observeTips()
        observeSugar()
    }

    private fun observeTips() = viewModelScope.launch {
        repo.tipsFlow(limit = 10)
            .onStart { _state.update { it.copy(loadingTips = true, errorTips = null) } }
            .catch { e -> _state.update { it.copy(loadingTips = false, errorTips = e.message) } }
            .collect { list: List<Tip> ->
                _state.update {
                    it.copy(
                        tips = list.map { t -> TipUi(t.id, t.imageUrl, t.text) },
                        loadingTips = false,
                        errorTips = null
                    )
                }
            }
    }

    private fun observeSugar() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _state.update {
                it.copy(
                    loadingSugar = false,
                    dayGrams = 0,
                    weekGrams = 0,
                    monthGrams = 0
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loadingSugar = true) }

            try {
                val today = LocalDate.now()

                // ====== HARI INI ======
                val dayTotal: Double =
                    trekRepository.getTotalSugarForDate(uid, today)

                // ====== MINGGU INI ======
                val weekStart = today.with(DayOfWeek.MONDAY)
                val weekEnd = weekStart.plusDays(6)
                val weekData = trekRepository.getTotalSugarForDateRange(
                    userId = uid,
                    startDate = weekStart,
                    endDate = weekEnd
                )
                val weekTotal: Double = weekData.sumOf { it.totalGram }

                // ====== BULAN INI ======
                val yearMonth = YearMonth.from(today)
                val monthStart = yearMonth.atDay(1)
                val monthEnd = yearMonth.atEndOfMonth()
                val monthData = trekRepository.getTotalSugarForDateRange(
                    userId = uid,
                    startDate = monthStart,
                    endDate = monthEnd
                )
                val monthTotal: Double = monthData.sumOf { it.totalGram }

                _state.update {
                    it.copy(
                        loadingSugar = false,
                        dayGrams = dayTotal.toInt().coerceAtLeast(0),
                        weekGrams = weekTotal.toInt().coerceAtLeast(0),
                        monthGrams = monthTotal.toInt().coerceAtLeast(0)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadingSugar = false) }
            }
        }
    }


    private inline fun <reified T> MutableStateFlow<T>.update(block: (T) -> T) {
        this.value = block(this.value)
    }
}