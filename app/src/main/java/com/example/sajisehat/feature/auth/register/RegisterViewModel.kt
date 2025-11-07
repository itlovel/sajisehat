package com.example.sajisehat.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RegisterStep(val percent: Int) { NAME(30), EMAIL(60), PASSWORD(90), DONE(100) }

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val step: RegisterStep = RegisterStep.NAME,
    val loading: Boolean = false,
    val error: String? = null,
    val rememberMe: Boolean = false
) {
    val stepPercent: Int get() = step.percent
}

class RegisterViewModel(
    private val repo: AuthRepository = AppGraph.authRepo
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun updateFirst(s: String)    { _state.value = _state.value.copy(firstName = s, error = null) }
    fun updateLast(s: String)     { _state.value = _state.value.copy(lastName = s, error = null) }
    fun updateEmail(s: String)    { _state.value = _state.value.copy(email = s, error = null) }
    fun updatePassword(s: String) { _state.value = _state.value.copy(password = s, error = null) }
    fun toggleRemember()          { _state.value = _state.value.copy(rememberMe = !_state.value.rememberMe) }

    fun moveToEmail()    { _state.value = _state.value.copy(step = RegisterStep.EMAIL) }
    fun moveToPassword() { _state.value = _state.value.copy(step = RegisterStep.PASSWORD) }
    fun markDone()       { _state.value = _state.value.copy(step = RegisterStep.DONE) }

    fun submit(onSuccess: () -> Unit) = viewModelScope.launch {
        val st = _state.value
        val display = (st.firstName + " " + st.lastName).trim()
        _state.value = st.copy(loading = true, error = null)
        val res = repo.registerEmail(st.email.trim(), st.password, display)
        _state.value = st.copy(loading = false, error = res.exceptionOrNull()?.localizedMessage)
        if (res.isSuccess) {
            markDone() // 100%
            onSuccess()
        }
    }
}
