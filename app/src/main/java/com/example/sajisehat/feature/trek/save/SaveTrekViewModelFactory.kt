package com.example.sajisehat.feature.trek.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.trek.TrekRepository

class SaveTrekViewModelFactory(
    private val trekRepository: TrekRepository,
    private val authRepository: AuthRepository,
    private val sugarGram: Double
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveTrekViewModel::class.java)) {
            return SaveTrekViewModel(
                trekRepository = trekRepository,
                authRepository = authRepository,
                initialSugarGram = sugarGram
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
