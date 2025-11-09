package com.example.sajisehat.feature.trek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository

class TrekManualViewModelFactory(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository,
    private val dateString: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrekManualViewModel::class.java)) {
            return TrekManualViewModel(
                trekRepository = trekRepository,
                authRepository = authRepository,
                dateString = dateString
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
