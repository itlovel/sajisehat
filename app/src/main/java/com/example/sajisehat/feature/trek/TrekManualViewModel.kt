package com.example.sajisehat.feature.trek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.data.trek.model.TrackedProduct
import com.example.sajisehat.feature.trek.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TrekManualViewModel(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository,
    private val dateString: String           // "yyyy-MM-dd" dari route
) : ViewModel() {

    private val date: LocalDate = LocalDate.parse(dateString)

    private val _inputState = MutableStateFlow(ManualInputUiState())
    val inputState: StateFlow<ManualInputUiState> = _inputState

    // nanti untuk layar kalkulasi / save,
    // sekarang kita siapkan saja:
    private var lastResult: ManualResultUi? = null

    fun onProductNameChange(newValue: String) {
        _inputState.update {
            it.copy(
                productName = newValue,
                errorMessage = null
            ).validate()
        }
    }

    fun onSugarPerServingChange(newValue: String) {
        _inputState.update {
            it.copy(
                sugarPerServingText = newValue,
                errorMessage = null
            ).validate()
        }
    }

    fun onServingSizeChange(newValue: String) {
        _inputState.update {
            it.copy(
                servingSizeText = newValue,
                errorMessage = null
            ).validate()
        }
    }

    private fun ManualInputUiState.validate(): ManualInputUiState {
        val sugar = sugarPerServingText.toDoubleOrNull()
        val nameOk = productName.isNotBlank()
        val sugarOk = sugar != null && sugar > 0.0

        return copy(
            isValid = nameOk && sugarOk
        )
    }

    /**
     * Hitung hasil manual (dipakai buat layar kalkulasi nantinya).
     * Sekarang kita cuma mengembalikan ManualResultUi, navigation diatur di App.kt.
     */
    fun buildManualResult(): ManualResultUi? {
        val state = _inputState.value

        val sugar = state.sugarPerServingText.toDoubleOrNull() ?: return null
        val servingSize = state.servingSizeText.toDoubleOrNull()

        val percent = (sugar / 50.0 * 100.0)
            .coerceIn(0.0, 100.0)
            .toInt()

        val level = when {
            sugar <= 0.0 -> SugarLevelUi.UNKNOWN
            sugar <= 5.0 -> SugarLevelUi.LOW
            sugar <= 15.0 -> SugarLevelUi.MEDIUM
            else -> SugarLevelUi.HIGH
        }

        val result = ManualResultUi(
            productName = state.productName,
            sugarPerServingGram = sugar,
            servingSizeGram = servingSize,
            percentOfDailyNeed = percent,
            level = level
        )

        lastResult = result
        return result
    }

    /**
     * Nanti dipakai di layar "Simpan ke Trek Manual".
     * Untuk sekarang belum dipanggil dari mana-mana, gpp.
     */
    fun saveToTrek(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = authRepository.currentUser
        if (user == null) {
            onError("User belum login")
            return
        }

        val result = lastResult ?: run {
            onError("Data manual belum dihitung")
            return
        }

        viewModelScope.launch {
            try {
                val entry = TrackedProduct(
                    userId = user.uid,
                    productName = result.productName,
                    sugarGram = result.sugarPerServingGram,
                    scannedAt = System.currentTimeMillis(),
                    date = date.toString()
                )
                trekRepository.addTrackedProduct(entry)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan produk ke trek")
            }
        }
    }

    fun getLastResult(): ManualResultUi? = lastResult

}
