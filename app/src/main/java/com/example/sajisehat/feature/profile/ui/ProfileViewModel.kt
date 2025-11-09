// feature/profile/ProfileViewModel.kt
package com.example.sajisehat.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val loading: Boolean = true,
    val error: String? = null
)

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() = viewModelScope.launch {
        val u = auth.currentUser

        // fallback nama kalau displayName kosong
        val niceName = when {
            !u?.displayName.isNullOrBlank() -> u!!.displayName!!
            !u?.email.isNullOrBlank() -> {
                val raw = u!!.email!!.substringBefore("@")
                raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
            else -> "Pengguna"
        }

        _state.update {
            it.copy(
                displayName = niceName,
                email = u?.email.orEmpty(),
                photoUrl = u?.photoUrl?.toString(),
                loading = false,
                error = if (u == null) "Belum login" else null
            )
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            onDone()
        }
    }
}
