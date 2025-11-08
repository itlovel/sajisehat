package com.example.sajisehat.feature.topbar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TopBarViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        TopBarState(
            greetingName = "Nofa Nisrina",
            subtitle = "Siap jaga asupan gulamu hari ini?",
            avatarUrl = null, // contoh: "https://picsum.photos/80"
            unreadCount = 3,
            showBack = false
        )
    )
    val state: StateFlow<TopBarState> = _state.asStateFlow()

    fun onEvent(e: TopBarEvent) {
        when (e) {
            TopBarEvent.OnBackClick -> { /* TODO: nav up dari screen */ }
            TopBarEvent.OnBellClick -> {
                // contoh: reset badge
                _state.value = _state.value.copy(unreadCount = 0)
            }
            TopBarEvent.OnAvatarClick -> {
                // contoh: buka profile
            }
        }
    }

    // Contoh API untuk update nama/subtitle bila user berubah
    fun setUser(name: String, avatarUrl: String?) {
        _state.value = _state.value.copy(greetingName = name, avatarUrl = avatarUrl)
    }

    fun setShowBack(show: Boolean) {
        _state.value = _state.value.copy(showBack = show)
    }
}
