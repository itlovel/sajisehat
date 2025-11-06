package com.example.sajisehat.feature.topbar

data class TopBarState(
    val greetingName: String = "Nofa Nisrina",
    val subtitle: String? = "Siap jaga asupan gulamu hari ini?",
    val avatarUrl: String? = null,     // pakai URL; kalau null akan pakai placeholder
    val unreadCount: Int = 0,
    val showBack: Boolean = false
)

sealed interface TopBarEvent {
    data object OnBackClick : TopBarEvent
    data object OnBellClick : TopBarEvent
    data object OnAvatarClick : TopBarEvent
}
