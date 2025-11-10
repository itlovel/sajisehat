package com.example.sajisehat.feature.trek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository

class TrekViewModelFactory(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrekViewModel::class.java)) {
            return TrekViewModel(
                trekRepository = trekRepository,
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}