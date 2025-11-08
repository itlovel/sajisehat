// feature/scan/ScanViewModel.kt
package com.example.sajisehat.feature.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.prefs.AppPrefs
import com.example.sajisehat.data.scan.ScanRepository
import com.example.sajisehat.data.scan.model.NutritionScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ScanViewModel(
    private val scanRepository: ScanRepository,
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState

    init {
        // Cek apakah kamera sudah pernah diizinkan sebelumnya
        viewModelScope.launch {
            val granted = appPrefs.cameraGranted.first()
            _uiState.update { state ->
                state.copy(
                    permissionStatus = if (granted) {
                        CameraPermissionStatus.GRANTED
                    } else {
                        CameraPermissionStatus.UNKNOWN
                    },
                    step = if (granted) {
                        ScanStep.SCANNING
                    } else {
                        ScanStep.PERMISSION
                    }
                )
            }
        }
    }

    /**
     * Dipanggil setelah dialog permission sistem selesai.
     *
     * @param granted          true jika user memberi izin CAMERA
     * @param permanentlyDenied true jika user pilih "Don't ask again"
     */
    fun onCameraPermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        viewModelScope.launch {
            if (granted) {
                appPrefs.setCameraGranted(true)
                _uiState.update {
                    it.copy(
                        permissionStatus = CameraPermissionStatus.GRANTED,
                        step = ScanStep.SCANNING,
                        errorMessage = null
                    )
                }
            } else {
                val status = if (permanentlyDenied) {
                    CameraPermissionStatus.PERMANENTLY_DENIED
                } else {
                    CameraPermissionStatus.DENIED
                }
                _uiState.update {
                    it.copy(
                        permissionStatus = status,
                        step = ScanStep.PERMISSION,
                        errorMessage = null
                    )
                }
            }
        }
    }

    /**
     * Dipanggil ketika ML Kit Document Scanner selesai
     * dan mengembalikan list Uri (halaman label yang discan).
     */
    fun onScanImagesResult(imageUris: List<Uri>) {
        if (imageUris.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(step = ScanStep.PROCESSING, isProcessing = true, errorMessage = null) }

            val result = scanRepository.processScannedImages(imageUris)
            result.fold(
                onSuccess = { nutrition ->
                    val ui = mapToUi(nutrition)
                    _uiState.update {
                        it.copy(
                            step = ScanStep.RESULT,
                            isProcessing = false,
                            lastResult = ui,
                            isExpandedInfo = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            step = ScanStep.SCANNING,
                            isProcessing = false,
                            errorMessage = e.message ?: "Gagal memproses hasil scan."
                        )
                    }
                }
            )
        }
    }

    fun onScanAgainClicked() {
        _uiState.update {
            it.copy(
                step = ScanStep.SCANNING,
                isProcessing = false,
                errorMessage = null,
                // lastResult bisa dibiarkan untuk history, atau direset:
                // lastResult = null,
                isExpandedInfo = false
            )
        }
    }

    fun onToggleExpanded() {
        _uiState.update { it.copy(isExpandedInfo = !it.isExpandedInfo) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ================= MAPPER =================

    private fun mapToUi(result: NutritionScanResult): ScanResultUi {
        val sugarPerServing = result.sugarPerServingGram
        val sugarPerPack = result.sugarPerPackGram
        val servings = result.servingsPerPack

        // contoh kebutuhan gula harian (bisa kamu ganti dengan standar resmi)
        val dailyLimit = 50.0
        val dailyPercent = sugarPerServing?.let { (it / dailyLimit * 100).roundToInt() }

        val level = sugarPerServing?.let { sugar ->
            when {
                sugar <= 5 -> SugarLevel.RENDAH
                sugar <= 15 -> SugarLevel.SEDANG
                else -> SugarLevel.TINGGI
            }
        }

        return ScanResultUi(
            productName = result.productName,
            servingSizeGram = result.servingSizeGram,
            servingsPerPack = servings,
            sugarPerServingGram = sugarPerServing,
            sugarPerPackGram = sugarPerPack,
            dailyPercent = dailyPercent,
            sugarLevel = level,
            rawText = result.rawText
        )
    }
}
