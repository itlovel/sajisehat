package com.example.sajisehat.feature.trek.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.data.trek.model.TrackedProduct
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SaveTrekViewModel(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository,
    private val initialSugarGram: Double
) : ViewModel() {

    var uiState: SaveTrekUiState = SaveTrekUiState(sugarGram = initialSugarGram)
        private set

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadInitial()
    }

    private fun loadInitial() {
        val user = authRepository.currentUser ?: return
        val today = LocalDate.now().format(dateFormatter)

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorMessage = null)

                val totalToday = trekRepository.getTodayTotalSugar(
                    userId = user.uid,
                    date = today
                )

                uiState = uiState.copy(
                    isLoading = false,
                    totalBefore = totalToday,
                    totalAfter = totalToday + uiState.sugarGram
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat data trek hari ini"
                )
            }
        }
    }

    fun onProductNameChange(newName: String) {
        uiState = uiState.copy(productName = newName)
    }

    fun save(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = authRepository.currentUser
        if (user == null) {
            onError("User belum login")
            return
        }

        val today = LocalDate.now().format(dateFormatter)
        val nowMillis = System.currentTimeMillis()

        val entry = TrackedProduct(
            userId = user.uid,
            productName = uiState.productName,
            sugarGram = uiState.sugarGram,
            scannedAt = nowMillis,
            date = today
        )

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorMessage = null)

                trekRepository.addTrackedProduct(entry)

                uiState = uiState.copy(
                    isLoading = false,
                    isSaved = true
                )
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menyimpan trek gula"
                )
                onError(uiState.errorMessage!!)
            }
        }
    }
}
