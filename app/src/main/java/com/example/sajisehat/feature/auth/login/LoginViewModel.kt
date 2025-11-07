package com.example.sajisehat.feature.auth.login

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    /**
     * Try Credential Manager first. If it fails, call [onFallback] so UI can launch
     * classic Google Sign-In (Play Services).
     */
    fun signInWithGoogle(
        activity: Activity,
        onFallback: (serverClientId: String, cause: Throwable?) -> Unit
    ) = viewModelScope.launch {
        try {
            _state.update { it.copy(isLoading = true, error = null) }

            val serverClientId = activity.getString(R.string.default_web_client_id)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val cm = CredentialManager.create(activity)
            val result = cm.getCredential(activity, request)
            val googleIdCred = GoogleIdTokenCredential.createFrom(result.credential.data)

            // Continue with Firebase
            signInWithFirebaseIdToken(googleIdCred.idToken)
        } catch (t: Throwable) {
            // hand to classic fallback
            val clientId = runCatching { activity.getString(R.string.default_web_client_id) }.getOrNull()
            _state.update { it.copy(isLoading = false) }
            if (clientId != null) onFallback(clientId, t)
            else setError(t.message ?: "Tidak bisa memulai Google Sign-In")
        }
    }

    /** Use this when you already have an ID token (from classic Google Sign-In). */
    fun signInWithFirebaseIdToken(idToken: String?) {
        if (idToken.isNullOrBlank()) {
            setError("No credentials available")
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnSuccessListener {
                _state.update { s -> s.copy(isLoading = false, isLoggedIn = true) }
            }
            .addOnFailureListener { e ->
                _state.update { s -> s.copy(isLoading = false, error = e.message) }
            }
    }

    // ---------- helpers for error UI ----------
    fun setError(message: String?) {
        _state.update { it.copy(error = message) }
    }
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
