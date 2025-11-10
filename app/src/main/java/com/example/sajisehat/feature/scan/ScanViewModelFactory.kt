// feature/scan/ScanViewModelFactory.kt
package com.example.sajisehat.feature.scan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sajisehat.di.AppGraph

class ScanViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            val repo = AppGraph.scanRepository(appContext)
            val prefs = AppGraph.prefs(appContext)
            return ScanViewModel(repo, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
