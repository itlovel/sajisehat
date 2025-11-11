package com.example.sajisehat.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.example.sajisehat.data.trek.model.DailySugarData
import com.example.sajisehat.di.AppGraph
import com.example.sajisehat.feature.trek.model.SugarLevelUi
import com.example.sajisehat.feature.trek.model.getDailySugarLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

data class DailyNotificationUi(
    val id: String,
    val date: LocalDate,
    val title: String,
    val message: String,
    val timeLabel: String = "21.00"
)

data class NotificationUiState(
    val loading: Boolean = true,
    val items: List<DailyNotificationUi> = emptyList(),
    val error: String? = null
)

class NotificationViewModel(
    private val trekRepository: TrekRepository = AppGraph.trekRepository,
    private val authRepository: AuthRepository = AppGraph.authRepo
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationUiState())
    val state: StateFlow<NotificationUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val user = authRepository.currentUser ?: run {
                _state.update {
                    it.copy(
                        loading = false,
                        error = "Belum login"
                    )
                }
                return@launch
            }

            _state.update { it.copy(loading = true, error = null) }

            try {
                val today = LocalDate.now()
                val start = today.minusDays(7) // 7 hari ke belakang

                val data: List<DailySugarData> =
                    trekRepository.getTotalSugarForDateRange(user.uid, start, today)

                val map = data.associateBy { it.date }

                val list = (0L..7L).map { offset ->
                    val date = start.plusDays(offset)
                    val totalGram = map[date]?.totalGram ?: 0.0
                    buildDailyNotification(date, totalGram)
                }.filterNotNull()
                    .sortedByDescending { it.date }

                _state.update { it.copy(loading = false, items = list) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        loading = false,
                        error = e.message ?: "Gagal memuat notifikasi"
                    )
                }
            }
        }
    }

    // ========= TEMPLATE TEKS NOTIF =========

    private fun buildDailyNotification(
        date: LocalDate,
        totalGram: Double
    ): DailyNotificationUi? {
        val level = getDailySugarLevel(totalGram)

        val templates: List<Pair<String, String>> = when (level) {
            SugarLevelUi.LOW -> lowTemplates
            SugarLevelUi.MEDIUM -> mediumTemplates
            SugarLevelUi.HIGH -> highTemplates
            SugarLevelUi.UNKNOWN -> lowTemplates
        }

        if (templates.isEmpty()) return null

        val (title, bodyPattern) = templates.random(Random(date.toEpochDay()))
        val body = bodyPattern.replace("{gram}", "%.0f".format(totalGram))

        return DailyNotificationUi(
            id = date.toString(),
            date = date,
            title = title,
            message = body
        )
    }

    private val lowTemplates = listOf(
        "Bagus Hari Ini!" to
                "Kamu hanya mengonsumsi sekitar {gram} gula hari ini. Pertahankan pola makan sehatmu ya! 🥕",
        "Pilihan Sehat!" to
                "Asupan gulamu hari ini cuma {gram}. Keputusanmu buat batasi manis sudah tepat, lanjutkan! 💪",
        "Mantap, Masih Aman" to
                "Total gula hari ini {gram}, masih dalam batas aman. Terus jaga pola makan dan tetap aktif bergerak ya! 🚶‍♂️"
    )

    private val mediumTemplates = listOf(
        "Nyaris Batas" to
                "Gula harianmu sudah {gram}. Sedikit lagi menyentuh batas 50g, yuk lebih pilih-pilih camilan besok. 💡",
        "Waspada Gula!" to
                "Hari ini kamu mengonsumsi {gram} gula. Masih oke, tapi coba kurangi minuman dan makanan manis di hari berikutnya ya. ⚖️",
        "Perlu Sedikit Rem" to
                "Konsumsimu {gram} hari ini. Ayo mulai kurangi porsi manis pelan-pelan supaya tetap dalam batas sehat. 🚦"
    )

    private val highTemplates = listOf(
        "Terlalu Manis Hari Ini" to
                "Total gula hari ini mencapai {gram}, melewati batas 50g. Yuk, besok kurangi minuman manis dan pilih makanan lebih sehat. 🍰➡🥗",
        "Gula Melampaui Batas" to
                "Wah, konsumsi gulamu {gram} hari ini. Tubuhmu butuh istirahat dari gula, coba batasi manis mulai besok ya. 🚫",
        "Saatnya Kurangi Manis" to
                "Hari ini kamu sudah mengonsumsi {gram} gula. Yuk jadikan ini pengingat untuk lebih bijak memilih makanan dan minuman ke depannya. 📉"
    )
}
