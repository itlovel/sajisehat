package com.example.sajisehat.feature.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmailLoginState(
    val email: String = "",
    val password: String = "",
    val remember: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)

class EmailLoginViewModel(
    private val repo: AuthRepository = AppGraph.authRepo
) : ViewModel() {

    private val _state = MutableStateFlow(EmailLoginState())
    val state = _state.asStateFlow()

    fun updateEmail(s: String)    = _state.update { it.copy(email = s, error = null) }
    fun updatePassword(s: String) = _state.update { it.copy(password = s, error = null) }
    fun toggleRemember()          = _state.update { it.copy(remember = !it.remember) }

    fun login(context: Context, onSuccess: () -> Unit) = viewModelScope.launch {
        val st = _state.value
        _state.update { it.copy(loading = true, error = null) }
        val res = repo.signInEmail(st.email.trim(), st.password)
        if (res.isSuccess) {
            if (st.remember) {
                // Simpan "remember me"
                AppGraph.prefs(context).setRememberMe(true)
            }
            _state.update { it.copy(loading = false) }
            onSuccess()
        } else {
            _state.update { it.copy(loading = false, error = res.exceptionOrNull()?.localizedMessage ?: "Login gagal") }
        }
    }
}
