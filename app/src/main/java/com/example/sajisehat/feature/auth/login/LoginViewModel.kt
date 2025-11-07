package com.example.sajisehat.feature.auth.login

import android.content.Context
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

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val serverClientId = context.getString(
                    // resource ini dibuat otomatis oleh google-services.json
                    R.string.default_web_client_id
                )

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)
                val googleIdCred =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCred =
                    GoogleAuthProvider.getCredential(googleIdCred.idToken, null)

                auth.signInWithCredential(firebaseCred)
                    .addOnSuccessListener {
                        _state.update { st -> st.copy(isLoading = false, isLoggedIn = true) }
                    }
                    .addOnFailureListener { e ->
                        _state.update { st -> st.copy(isLoading = false, error = e.message) }
                    }
            } catch (t: Throwable) {
                _state.update { it.copy(isLoading = false, error = t.message) }
            }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}
