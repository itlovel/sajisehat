package com.example.sajisehat.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.home.FirebaseHomeRepository
import com.example.sajisehat.data.home.HomeRepository
import com.example.sajisehat.data.home.SugarSummary
import com.example.sajisehat.data.home.Tip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

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
    private val repo: HomeRepository = FirebaseHomeRepository()
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
            _state.update { it.copy(loadingSugar = false, dayGrams = 0, weekGrams = 0, monthGrams = 0) }
            return
        }
        viewModelScope.launch {
            repo.sugarSummaryFlow(uid)
                .onStart { _state.update { it.copy(loadingSugar = true) } }
                .catch { _state.update { it.copy(loadingSugar = false) } }
                .collect { s: SugarSummary ->
                    _state.update {
                        it.copy(
                            loadingSugar = false,
                            dayGrams = s.day.coerceAtLeast(0),
                            weekGrams = s.week.coerceAtLeast(0),
                            monthGrams = s.month.coerceAtLeast(0)
                        )
                    }
                }
        }
    }

    private inline fun <reified T> MutableStateFlow<T>.update(block: (T) -> T) {
        this.value = block(this.value)
    }
}