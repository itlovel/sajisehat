package com.example.sajisehat.feature.trek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.feature.trek.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TrekDetailViewModel(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository,
    dateString: String
) : ViewModel() {

    private val date: LocalDate = LocalDate.parse(dateString)

    private val _uiState = MutableStateFlow(
        TrekDetailUiState(
            isLoading = true,
            date = date
        )
    )
    val uiState: StateFlow<TrekDetailUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User belum login"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // total gram hari ini
                val totalGram = trekRepository.getTotalSugarForDate(user.uid, date)
                val todaySummary = buildTodaySummary(totalGram)

                // daftar item per hari
                val products = trekRepository.getTrackedProductsForDate(user.uid, date)

                val itemsUi = products.map { p ->
                    val percent = (p.sugarGram / 50.0 * 100.0)
                        .coerceIn(0.0, 100.0)
                        .toInt()

                    TrekDetailItemUi(
                        id = p.id,
                        productName = p.productName,
                        sugarGram = p.sugarGram,
                        percentageOfDailyNeed = percent
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todaySummary = todaySummary,
                        items = itemsUi,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Terjadi kesalahan saat memuat detail trek"
                    )
                }
            }
        }
    }

    fun onDeleteItem(itemId: String) {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: return@launch

            try {
                trekRepository.deleteTrackedProduct(user.uid, itemId)
                // setelah delete, reload data
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "Gagal menghapus item"
                    )
                }
            }
        }
    }

    // sama seperti di TrekViewModel, tapi disalin di sini supaya ViewModel ini mandiri
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
}
